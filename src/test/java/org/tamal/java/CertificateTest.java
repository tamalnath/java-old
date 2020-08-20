package org.tamal.java;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class CertificateTest {
    private Path dir;

    @BeforeClass
    void init() throws IOException, InterruptedException {
        dir = Files.createTempDirectory("certificate-");
        Path script = dir.resolve("generate-certificates.sh");
        Files.copy(getClass().getResourceAsStream("/generate-certificates.sh"), script);
        Process process = new ProcessBuilder("bash", script.toString()).directory(dir.toFile()).inheritIO().start();
        assertEquals(process.waitFor(), 0);
    }

    @Test
    void testValidCertificateChain() throws IOException, GeneralSecurityException {
        verifyCertificateChain(dir.resolve("rootca.crt"), dir.resolve("server.crt"), dir.resolve("intermediate0.crt"));
    }

    @Test(expectedExceptions = CertPathValidatorException.class)
    void testCertificateChainInWrongOrder() throws IOException, GeneralSecurityException {
        try {
            verifyCertificateChain(dir.resolve("rootca.crt"), dir.resolve("intermediate0.crt"), dir.resolve("server.crt"));
        } catch (CertPathValidatorException e) {
            assertEquals(e.getReason(), PKIXReason.NO_TRUST_ANCHOR);
            throw e;
        }
    }

    @Test(expectedExceptions = CertPathValidatorException.class)
    void testCertificateMissingIntermediateCA() throws IOException, GeneralSecurityException {
        try {
            verifyCertificateChain(dir.resolve("rootca.crt"), dir.resolve("server.crt"));
        } catch (CertPathValidatorException e) {
            assertEquals(e.getReason(), PKIXReason.NO_TRUST_ANCHOR);
            throw e;
        }
    }

    @Test(expectedExceptions = CertPathValidatorException.class)
    void testCertificatePathLength() throws IOException, GeneralSecurityException {
        try {
            verifyCertificateChain(dir.resolve("rootca0.crt"), dir.resolve("server.crt"), dir.resolve("intermediate.crt"));
        } catch (CertPathValidatorException e) {
            assertEquals(e.getReason(), PKIXReason.NAME_CHAINING);
            throw e;
        }
    }

    @Test
    void testCertificateNoIssuerExtension() throws IOException, GeneralSecurityException {
        verifyCertificateChain(dir.resolve("rootca-no-ext.crt"), dir.resolve("server-no-issuer-ext.crt"));
    }

    @Test
    void testCertificateIssuerNotCA() throws IOException, GeneralSecurityException {
        verifyCertificateChain(dir.resolve("not-ca.crt"), dir.resolve("server-issuer-not-ca.crt"));
    }

    @Test
    void testCertificateWrongKeyUsage() throws IOException, GeneralSecurityException {
        verifyCertificateChain(dir.resolve("rootca-wrong-keyUsage.crt"), dir.resolve("wrong-issuer-keyUsage.crt"));
    }

    @Test
    void testCertificateWrongExtendedKeyUsage() throws IOException, GeneralSecurityException {
        verifyCertificateChain(dir.resolve("rootca-wrong-extendedKeyUsage.crt"), dir.resolve("wrong-issuer-extendedKeyUsage.crt"));
    }

    /**
     * Verifies certificate chain against a trusted certificate.
     * @param trustedCert the trusted certificate or the Root CA
     * @param certPaths the certificate paths
     * @return result of the validation algorithm
     * @throws IOException if the file cannot be read
     * @throws GeneralSecurityException if the certificate cannot be parsed or the validation fails
     */
    private CertPathValidatorResult verifyCertificateChain(Path trustedCert, Path... certPaths) throws IOException, GeneralSecurityException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate rootCA;
        try (InputStream inputStream = new FileInputStream(trustedCert.toFile())) {
            Collection<? extends Certificate> certs = factory.generateCertificates(inputStream);
            assertEquals(certs.size(), 1);
            rootCA = (X509Certificate) certs.iterator().next();
        }
        Set<TrustAnchor> anchors = Collections.singleton(new TrustAnchor(rootCA, null));
        List<Certificate> certificates = new ArrayList<>();
        for (Path certPath : certPaths) {
            try (InputStream inputStream = new FileInputStream(certPath.toFile())) {
                certificates.addAll(factory.generateCertificates(inputStream));
            }
        }
        CertPath certPath = factory.generateCertPath(certificates);
        X509CertSelector targetConstraints = new X509CertSelector();
        targetConstraints.setCertificateValid(new Date());
        PKIXParameters pkixParameters = new PKIXParameters(anchors);
        pkixParameters.setTargetCertConstraints(targetConstraints);
        pkixParameters.setRevocationEnabled(false);
        CertPathValidator validator = CertPathValidator.getInstance("PKIX");
        return validator.validate(certPath, pkixParameters);
    }
}

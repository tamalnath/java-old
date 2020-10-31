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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CertificateTest {
    private Path dir;

    @BeforeClass
    void init() throws IOException, InterruptedException {
        dir = Files.createTempDirectory("certificate-");
        System.out.println(dir);
        Path script = dir.resolve("generate-certificates.sh");
        Files.copy(getClass().getResourceAsStream("/generate-certificates.sh"), script);
        Process process = new ProcessBuilder("bash", "-x", script.toString()).directory(dir.toFile()).inheritIO().start();
        assertEquals(process.waitFor(), 0);
    }

    @Test
    void testValidCertificateChain() throws IOException, GeneralSecurityException {
        validate(dir.resolve("server.crt"), dir.resolve("intermediate0.crt"), dir.resolve("rootca.crt"));
    }

    @Test(expectedExceptions = CertPathValidatorException.class)
    void testCertificateChainInWrongOrder() throws IOException, GeneralSecurityException {
        try {
            validate(dir.resolve("intermediate0.crt"), dir.resolve("server.crt"), dir.resolve("rootca.crt"));
        } catch (CertPathValidatorException e) {
             assertEquals(e.getReason(), PKIXReason.NO_TRUST_ANCHOR);
            throw e;
        }
    }

    @Test(expectedExceptions = CertPathValidatorException.class)
    void testCertificateMissingIntermediateCA() throws IOException, GeneralSecurityException {
        try {
            validate(dir.resolve("server.crt"), dir.resolve("rootca.crt"));
        } catch (CertPathValidatorException e) {
            assertEquals(e.getReason(), PKIXReason.NO_TRUST_ANCHOR);
            throw e;
        }
    }

    @Test(expectedExceptions = CertPathValidatorException.class)
    void testCertificatePathLength() throws IOException, GeneralSecurityException {
        try {
            validate(dir.resolve("server0.crt"), dir.resolve("intermediate.crt"), dir.resolve("intermediate0.crt"), dir.resolve("rootca.crt"));
        } catch (CertPathValidatorException e) {
            assertEquals(e.getReason(), PKIXReason.PATH_TOO_LONG);
            throw e;
        }
    }

    /**
     * Verifies certificate chain against a trusted certificate.
     * @param certificates the certificate chain in server-intermediate-root order
     * @return result of the validation algorithm
     * @throws IOException if the file cannot be read
     * @throws GeneralSecurityException if the certificate cannot be parsed or the validation fails
     */
    private CertPathValidatorResult validate(Path... certificates) throws IOException, GeneralSecurityException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        List<Certificate> certs = new ArrayList<>();
        for (Path certPath : certificates) {
            try (InputStream inputStream = new FileInputStream(certPath.toFile())) {
                certs.addAll(factory.generateCertificates(inputStream));
            }
        }
        assertTrue(certs.size() > 1);
        X509Certificate rootCA = (X509Certificate) certs.remove(certs.size() - 1);
        Set<TrustAnchor> anchors = Collections.singleton(new TrustAnchor(rootCA, null));
        CertPath certPath = factory.generateCertPath(certs);
        X509CertSelector x509CertSelector = new X509CertSelector();
        x509CertSelector.setCertificateValid(new Date());
        PKIXParameters pkixParameters = new PKIXParameters(anchors);
        pkixParameters.setTargetCertConstraints(x509CertSelector);
        pkixParameters.setRevocationEnabled(false);
        CertPathValidator validator = CertPathValidator.getInstance("PKIX");
        return validator.validate(certPath, pkixParameters);
    }
}

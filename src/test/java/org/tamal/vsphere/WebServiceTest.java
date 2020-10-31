package org.tamal.vsphere;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WebServiceTest {

    private SSLSocketFactory factory;
    private final HostnameVerifier verifier = (hostname, sslSession) -> true;
    private byte[] login;
    private byte[] findDns;
    private byte[] findIp;
    private static final int TIMEOUT_MS = 20_000;

    @BeforeClass
    void init() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        TrustManager[] trustManagers = { trustManager };
        context.init(null, trustManagers, new SecureRandom());
        factory = context.getSocketFactory();
    }

    @Test
    public void findDnsName() throws UnknownHostException {
        String hostname = System.getProperty("hostname", "vcenter.eng.vmware.com");
        int prefix = Integer.parseInt(System.getProperty("subnet", "22"));
        String username = System.getProperty("username", "root");
        String password = System.getProperty("password", "ca$hc0w");

        login = toString(getClass().getResourceAsStream("/login.xml"))
                .replace("${username}", username)
                .replace("${password}", password).getBytes();
        findDns = toString(getClass().getResourceAsStream("/find-by-dns.xml"))
                .replace("${dnsName}", hostname).getBytes();
        findIp = toString(getClass().getResourceAsStream("/find-by-ip.xml"))
                .replace("${ip}", InetAddress.getByName(hostname).getHostAddress()).getBytes();

        Map<String, Set<String>> status = new ConcurrentHashMap<>();
        Set<String> ips = getIPs(hostname, prefix);
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 16);
        AtomicInteger count = new AtomicInteger();
        long start = System.currentTimeMillis();
        for (String ip : ips) {
            exec.submit(() -> {
                try {
                    count.incrementAndGet();
                    URL url = new URL("https://" + ip + "/sdk/");
                    String session = login(url);
                    if (session == null) {
                        status.computeIfAbsent("Login failed", k -> new ConcurrentSkipListSet<>()).add(ip);
                        return;
                    }
                    boolean found = findVM(url, session, false);
                    if (found) {
                        System.out.println("Found VM in " + ip);
                        exec.shutdownNow();
                        return;
                    }
                    status.computeIfAbsent("Not Found by DNS Name", k -> new ConcurrentSkipListSet<>()).add(ip);
                    found = findVM(url, session, true);
                    if (found) {
                        System.out.println("Found VM in " + ip);
                        exec.shutdownNow();
                        return;
                    }
                    status.computeIfAbsent("Not Found by IP Address", k -> new ConcurrentSkipListSet<>()).add(ip);
                } catch (IOException e) {
                    status.computeIfAbsent(e.getMessage(), k -> new ConcurrentSkipListSet<>()).add(ip);
                }
            });
        }
        exec.shutdown();
        while (true) {
            try {
                if (exec.awaitTermination(30, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                System.err.println("Interrupted: " + e);
                exec.shutdownNow();
            }
            long now = System.currentTimeMillis();
            System.err.printf("%tr Scanned %d hosts (%d%%), Rate: %d hosts/sec%n", now, count.get(),
                    count.get() * 100 / ips.size(), count.get() * 1000 / (now - start));
            status.forEach((k, v) -> System.out.printf("%s -> %d%n", k, v.size()));
        }
        long now = System.currentTimeMillis();
        System.err.printf("%tr Scanning Completed. Scanned %d hosts, Rate: %d hosts/sec%n", now, count.get(),
                count.get() * 1000 / (now - start));
        status.forEach((k, v) -> System.out.printf("%s -> %s%n", k, v));
    }

    private static Set<String> getIPs(String host, int prefix) throws UnknownHostException {
        Set<String> ips = new TreeSet<>();
        InetAddress inetAddress = InetAddress.getByName(host);
        byte[] b = inetAddress.getAddress();
        int ip = b[0] << 24 | b[1] << 16 | b[2] << 8 | b[3];
        int suffix = 32 - prefix;
        int netmask = 0xFFFFFFFF << suffix;
        int low = ip & netmask;
        for (int i = 0; i < (1 << suffix); i++) {
            ip = low + i;
            String str = String.format("%d.%d.%d.%d", ip >>> 24, ip >>> 16 & 0xFF, ip >>> 8 & 0xFF, ip & 0xFF);
            ips.add(str);
        }
        return ips;
    }

    private String login(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(factory);
        connection.setHostnameVerifier(verifier);
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.setDoOutput(true);
        connection.getOutputStream().write(login);
        int code = connection.getResponseCode();
        String setCookie = connection.getHeaderField("Set-Cookie");
        if (code == 200 && setCookie != null) {
            List<HttpCookie> cookies = HttpCookie.parse(setCookie);
            for (HttpCookie cookie : cookies) {
                if ("vmware_soap_session".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean findVM(URL url, String session, boolean findByIp) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(factory);
        connection.setHostnameVerifier(verifier);
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.addRequestProperty("Cookie", "vmware_soap_session='" + session + "'");
        connection.setDoOutput(true);
        connection.getOutputStream().write(findByIp ? findIp : findDns);
        int code = connection.getResponseCode();
        if (code != 200) {
            return false;
        }
        String payload = toString(connection.getInputStream());
        boolean isEmpty;
        if (findByIp) {
            isEmpty = payload.contains("<FindAllByIpResponse xmlns=\"urn:vim25\"></FindAllByIpResponse>");
        } else {
            isEmpty = payload.contains("<FindAllByDnsNameResponse xmlns=\"urn:vim25\"></FindAllByDnsNameResponse>");
        }
        return !isEmpty;
    }

    private String toString(InputStream stream) {
        return new Scanner(stream).useDelimiter("\\A").next();
    }

}

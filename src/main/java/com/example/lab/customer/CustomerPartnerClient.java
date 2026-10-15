package com.example.lab.customer;

import com.example.lab.shared.PartnerClientConfig;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Properties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class CustomerPartnerClient {
    public HttpRequest shipmentRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("training-customer-token-001");
        return HttpRequest.newBuilder(URI.create("https://customer.example.test/events"))
                .headers(headers.entrySet().stream()
                        .flatMap(entry -> entry.getValue().stream().flatMap(value -> java.util.stream.Stream.of(entry.getKey(), value)))
                        .toArray(String[]::new)).GET().build();
    }

    public PasswordAuthentication legacyLogin() {
        char[] password = "training-customer-password-002".toCharArray();
        return new PasswordAuthentication("customer-worker", password);
    }

    public Properties archiveConnection() {
        Properties properties = new Properties();
        properties.setProperty("user", "customer-archive");
        properties.setProperty("password", "training-customer-archive-003");
        return properties;
    }

    public PartnerClientConfig partnerConfiguration() {
        return new PartnerClientConfig().withApiKey("training-customer-api-004");
    }

    public HttpRequest reconciliationRequest() {
        String token = "training-customer-reconciliation-005";
        return HttpRequest.newBuilder(URI.create("https://customer.example.test/reconcile"))
                .header("X-Api-Key", token).GET().build();
    }

    private String legacyToken() {
        return "training-customer-reconciliation-005";
    }

    public PasswordAuthentication runtimeLogin() {
        String password = System.getenv("PARTNER_CUSTOMER_PASSWORD");
        if (password == null) throw new IllegalStateException("Partner password is not configured");
        return new PasswordAuthentication("customer-worker", password.toCharArray());
    }

    public String passwordLabel() {
        FormLabels labels = new FormLabels();
        labels.setPassword("Enter your password");
        return labels.password;
    }
    private static class FormLabels {
        private String password;
        void setPassword(String label) { this.password = label; }
    }
}

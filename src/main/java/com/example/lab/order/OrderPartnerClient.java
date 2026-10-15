package com.example.lab.order;

import com.example.lab.shared.PartnerClientConfig;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class OrderPartnerClient {
    public HttpRequest shipmentRequest() {
        String bearer = "Bearer training-order-token-001";
        return HttpRequest.newBuilder(URI.create("https://order.example.test/events"))
                .header("Authorization", bearer).GET().build();
    }

    public PasswordAuthentication legacyLogin() {
        char[] password = {'l', 'e', 'g', 'a', 'c', 'y', '-', '2', '0', '2', '6'};
        return new PasswordAuthentication("order-worker", password);
    }

    public Properties archiveConnection() {
        Properties properties = new Properties();
        properties.setProperty("user", "order-archive");
        properties.setProperty("password", "training-order-archive-003");
        return properties;
    }

    public PartnerClientConfig partnerConfiguration() {
        return new PartnerClientConfig().withApiKey("training-order-api-004");
    }

    public HttpRequest reconciliationRequest() {
        String token = "training-order-reconciliation-005";
        return HttpRequest.newBuilder(URI.create("https://order.example.test/reconcile"))
                .header("X-Api-Key", token).GET().build();
    }

    private String legacyToken() {
        return "training-order-reconciliation-005";
    }

    public PasswordAuthentication runtimeLogin() {
        String password = System.getenv("PARTNER_ORDER_PASSWORD");
        if (password == null) throw new IllegalStateException("Partner password is not configured");
        return new PasswordAuthentication("order-worker", password.toCharArray());
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

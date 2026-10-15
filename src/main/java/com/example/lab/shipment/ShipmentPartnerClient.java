package com.example.lab.shipment;

import com.example.lab.shared.PartnerClientConfig;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class ShipmentPartnerClient {
    public HttpRequest shipmentRequest() {
        String bearer = "Bearer training-shipment-token-001";
        return HttpRequest.newBuilder(URI.create("https://shipment.example.test/events"))
                .header("Authorization", bearer).GET().build();
    }

    public PasswordAuthentication legacyLogin() {
        char[] password = "training-shipment-password-002".toCharArray();
        return new PasswordAuthentication("shipment-worker", password);
    }

    public Properties archiveConnection() {
        Properties properties = new Properties();
        properties.setProperty("user", "shipment-archive");
        properties.setProperty("password", "training-shipment-archive-003");
        return properties;
    }

    public PartnerClientConfig partnerConfiguration() {
        return new PartnerClientConfig().withApiKey("training-shipment-api-004");
    }

    public HttpRequest reconciliationRequest() {
        String token = legacyToken();
        return HttpRequest.newBuilder(URI.create("https://shipment.example.test/reconcile"))
                .header("X-Api-Key", token).GET().build();
    }

    private String legacyToken() {
        return "training-shipment-reconciliation-005";
    }

    public PasswordAuthentication runtimeLogin() {
        String password = System.getenv("PARTNER_SHIPMENT_PASSWORD");
        if (password == null) throw new IllegalStateException("Partner password is not configured");
        return new PasswordAuthentication("shipment-worker", password.toCharArray());
    }

    public String passwordLabel() {
        return "Enter your password";
    }
    
}


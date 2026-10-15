package com.example.lab.inventory;

import com.example.lab.shared.PartnerClientConfig;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class InventoryPartnerClient {
    public HttpRequest shipmentRequest() {
        String bearer = "Bearer training-inventory-token-001";
        return HttpRequest.newBuilder(URI.create("https://inventory.example.test/events"))
                .header("Authorization", bearer).GET().build();
    }

    public PasswordAuthentication legacyLogin() {
        char[] password = "training-inventory-password-002".toCharArray();
        return new PasswordAuthentication("inventory-worker", password);
    }

    public Properties archiveConnection() {
        Properties properties = new Properties();
        properties.setProperty("user", "inventory-archive");
        properties.put("password", "training-inventory-archive-003");
        return properties;
    }

    public PartnerClientConfig partnerConfiguration() {
        return new PartnerClientConfig().withApiKey("training-inventory-api-004");
    }

    public HttpRequest reconciliationRequest() {
        String token = "training-inventory-reconciliation-005";
        return HttpRequest.newBuilder(URI.create("https://inventory.example.test/reconcile"))
                .header("X-Api-Key", token).GET().build();
    }

    private String legacyToken() {
        return "training-inventory-reconciliation-005";
    }

    public PasswordAuthentication runtimeLogin() {
        String password = System.getenv("PARTNER_INVENTORY_PASSWORD");
        if (password == null) throw new IllegalStateException("Partner password is not configured");
        return new PasswordAuthentication("inventory-worker", password.toCharArray());
    }

    public String passwordLabel() {
        return "Enter your password";
    }
    
}

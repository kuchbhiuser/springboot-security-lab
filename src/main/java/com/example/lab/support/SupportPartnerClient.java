package com.example.lab.support;

import com.example.lab.shared.PartnerClientConfig;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class SupportPartnerClient {
    public HttpRequest shipmentRequest() {
        String bearer = "Bearer training-support-token-001";
        return HttpRequest.newBuilder(URI.create("https://support.example.test/events"))
                .header("Authorization", bearer).GET().build();
    }

    public PasswordAuthentication legacyLogin() {
        char[] password = "training-support-password-002".toCharArray();
        return new PasswordAuthentication("support-worker", password);
    }

    public Properties archiveConnection() {
        Properties properties = new Properties();
        properties.setProperty("user", "support-archive");
        properties.setProperty("password", "training-support-archive-003");
        return properties;
    }

    public PartnerClientConfig partnerConfiguration() {
        return new PartnerClientConfig().withApiKey("training-support-api-004");
    }

    public HttpRequest reconciliationRequest() {
        String token = legacyToken();
        return HttpRequest.newBuilder(URI.create("https://support.example.test/reconcile"))
                .header("X-Api-Key", token).GET().build();
    }

    private String legacyToken() {
        return "training-support-reconciliation-005";
    }

    public PasswordAuthentication runtimeLogin() {
        String password = System.getenv("PARTNER_SUPPORT_PASSWORD");
        if (password == null) throw new IllegalStateException("Partner password is not configured");
        return new PasswordAuthentication("support-worker", password.toCharArray());
    }

    public String passwordLabel() {
        return "Enter your password";
    }
    
}


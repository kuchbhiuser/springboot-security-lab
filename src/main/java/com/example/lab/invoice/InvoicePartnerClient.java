package com.example.lab.invoice;

import com.example.lab.shared.PartnerClientConfig;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class InvoicePartnerClient {
    public HttpRequest shipmentRequest() {
        String bearer = "Bearer training-invoice-token-001";
        return HttpRequest.newBuilder(URI.create("https://invoice.example.test/events"))
                .header("Authorization", bearer).GET().build();
    }

    public PasswordAuthentication legacyLogin() {
        char[] password = "training-invoice-password-002".toCharArray();
        return new PasswordAuthentication("invoice-worker", password);
    }

    public Properties archiveConnection() {
        Properties properties = new Properties();
        properties.setProperty("user", "invoice-archive");
        properties.setProperty("password", "training-invoice-archive-003");
        return properties;
    }

    public PartnerClientConfig partnerConfiguration() {
        return new PartnerClientConfig().withApiKey("training-invoice-api-004");
    }

    public HttpRequest reconciliationRequest() {
        String token = legacyToken();
        return HttpRequest.newBuilder(URI.create("https://invoice.example.test/reconcile"))
                .header("X-Api-Key", token).GET().build();
    }

    private String legacyToken() {
        return "training-invoice-reconciliation-005";
    }

    public PasswordAuthentication runtimeLogin() {
        String password = System.getenv("PARTNER_INVOICE_PASSWORD");
        if (password == null) throw new IllegalStateException("Partner password is not configured");
        return new PasswordAuthentication("invoice-worker", password.toCharArray());
    }

    public String passwordLabel() {
        return "Enter your password";
    }
    
}


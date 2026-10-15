package com.example.lab.shipment;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipment")
public class ShipmentController {
    private final ShipmentService service;
    private final ShipmentPartnerClient partner;
    public ShipmentController(ShipmentService service, ShipmentPartnerClient partner) {
        this.service = service;
        this.partner = partner;
    }

    @GetMapping("/lookup")
    public Object lookup(HttpServletRequest request) { return service.lookup(request); }
    @GetMapping("/search")
    public Object search(HttpServletRequest request) { return service.search(request); }
    @GetMapping("/sort")
    public Object sort(HttpServletRequest request) { return service.sort(request); }
    @PostMapping("/notes")
    public Object notes(HttpServletRequest request) { return Map.of("updated", service.updateNotes(request)); }
    @GetMapping("/report")
    public Object report(HttpServletRequest request) { return service.report(request); }
    @GetMapping("/safe-lookup")
    public Object safeLookup(HttpServletRequest request) { return service.safeLookup(request); }
    @GetMapping("/safe-sort")
    public Object safeSort(HttpServletRequest request) { return service.safeSort(request); }

    @GetMapping("/integration-preview")
    public Object integrationPreview() {
        // Build integration configuration locally; never transmit a request or expose secrets.
        return Map.of("eventTarget", partner.shipmentRequest().uri().toString(),
                "reconciliationTarget", partner.reconciliationRequest().uri().toString(),
                "legacyUser", partner.legacyLogin().getUserName(),
                "archiveUser", partner.archiveConnection().getProperty("user"),
                "partnerConfigured", partner.partnerConfiguration().configured(),
                "passwordLabel", partner.passwordLabel());
    }
}


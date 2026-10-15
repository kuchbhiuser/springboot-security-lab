package com.example.lab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OperationsApplicationTest {
    @Autowired MockMvc mvc;
    @Autowired com.example.lab.customer.CustomerPartnerClient customerPartner;
    @Autowired com.example.lab.order.OrderPartnerClient orderPartner;
    @Autowired com.example.lab.inventory.InventoryPartnerClient inventoryPartner;

    @Test void additionalSqlCoverageGapsAreStillExploitable() throws Exception {
        mvc.perform(get("/api/inventory/search").param("term", "missing%' OR 1=1 -- "))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/order/report").param("minimum", "999999 OR 1=1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test void missedCredentialFormsStillConfigureFixedSecrets() {
        org.junit.jupiter.api.Assertions.assertEquals("Bearer training-customer-token-001",
            customerPartner.shipmentRequest().headers().firstValue("Authorization").orElseThrow());
        org.junit.jupiter.api.Assertions.assertArrayEquals("legacy-2026".toCharArray(),
            orderPartner.legacyLogin().getPassword());
        org.junit.jupiter.api.Assertions.assertEquals("training-inventory-archive-003",
            inventoryPartner.archiveConnection().getProperty("password"));
    }

    @Test void allBusinessAreasAreWiredToDatabaseAndIntegrationConfiguration() throws Exception {
        for (String domain : new String[]{"customer", "order", "inventory", "invoice", "shipment", "support"}) {
            mvc.perform(get("/api/" + domain + "/lookup").param("value", "sample-" + domain))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
            mvc.perform(get("/api/" + domain + "/search").param("term", "Demo"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
            mvc.perform(get("/api/" + domain + "/sort").param("column", "id"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
            mvc.perform(get("/api/" + domain + "/report").param("minimum", "150"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
            mvc.perform(post("/api/" + domain + "/notes").param("id", "1").param("note", "updated"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.updated").value(1));
            mvc.perform(get("/api/" + domain + "/integration-preview"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.partnerConfigured").value(true));
        }
    }

    @Test void vulnerableLookupChangesMeaningButBoundLookupKeepsInputAsData() throws Exception {
        String input = "missing' OR '1'='1";
        mvc.perform(get("/api/customer/lookup").param("value", input))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/customer/safe-lookup").param("value", input))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void crossMethodReportIsReachableAndVulnerable() throws Exception {
        mvc.perform(get("/api/invoice/report").param("minimum", "999999 OR 1=1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }
}

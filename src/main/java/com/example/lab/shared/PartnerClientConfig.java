package com.example.lab.shared;

public class PartnerClientConfig {
    private String apiKey;
    public PartnerClientConfig withApiKey(String value) { this.apiKey = value; return this; }
    public boolean configured() { return apiKey != null && !apiKey.isBlank(); }
}

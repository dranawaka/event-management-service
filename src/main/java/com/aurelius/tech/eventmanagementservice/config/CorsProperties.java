package com.aurelius.tech.eventmanagementservice.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * Allowed origin patterns for cross-origin requests.
     */
    private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:3000"));

    /**
     * Allowed HTTP methods for cross-origin requests.
     */
    private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    /**
     * Allowed headers incoming requests can contain.
     */
    private List<String> allowedHeaders = new ArrayList<>(List.of("Authorization", "Content-Type", "Cache-Control"));

    /**
     * Headers exposed to the client.
     */
    private List<String> exposedHeaders = new ArrayList<>();

    /**
     * Whether cookies/credentials are allowed for cross-origin calls.
     */
    private boolean allowCredentials = true;

    /**
     * Max age (seconds) browsers can cache the pre-flight response.
     */
    private long maxAge = 3600;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public List<String> getExposedHeaders() {
        return exposedHeaders;
    }

    public void setExposedHeaders(List<String> exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
}





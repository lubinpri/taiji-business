package com.dbms.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 限流配置属性
 */
@Configuration
@ConfigurationProperties(prefix = "gateway.rate-limit")
public class RateLimitConfig {

    private boolean enabled = true;
    private int defaultLimit = 100;
    private int defaultReplenishRate = 10;
    private int defaultBurstCapacity = 20;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public int getDefaultReplenishRate() {
        return defaultReplenishRate;
    }

    public void setDefaultReplenishRate(int defaultReplenishRate) {
        this.defaultReplenishRate = defaultReplenishRate;
    }

    public int getDefaultBurstCapacity() {
        return defaultBurstCapacity;
    }

    public void setDefaultBurstCapacity(int defaultBurstCapacity) {
        this.defaultBurstCapacity = defaultBurstCapacity;
    }
}

package com.effective.festive.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "festival")
public class FestivalConfig {
    
    private Csv csv = new Csv();
    private Cache cache = new Cache();
    
    public Csv getCsv() {
        return csv;
    }
    
    public void setCsv(Csv csv) {
        this.csv = csv;
    }
    
    public Cache getCache() {
        return cache;
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public static class Csv {
        private String filePath = "busan_festivals.csv";
        private String encoding = "UTF-8";
        
        public String getFilePath() {
            return filePath;
        }
        
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
        
        public String getEncoding() {
            return encoding;
        }
        
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
    }
    
    public static class Cache {
        private boolean enabled = true;
        private long refreshInterval = 3600000; // 1시간 (밀리초)
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public long getRefreshInterval() {
            return refreshInterval;
        }
        
        public void setRefreshInterval(long refreshInterval) {
            this.refreshInterval = refreshInterval;
        }
    }
} 
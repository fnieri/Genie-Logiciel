package ulb.infof307.g04.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class CacheService {
    private long expirationTimeInMinutes;
    private boolean enabled;
    private final Map<String, CacheItem> cache = new HashMap<>();

    private record CacheItem(String value, Instant timestamp) {
    }

    // Singleton pattern
    private static CacheService instance;

    private CacheService() {
        // Set default expiration time
        this.expirationTimeInMinutes = 5;
        this.enabled = true;
    }

    public static synchronized CacheService getInstance() {
        if (instance == null) {
            instance = new CacheService();
        }
        return instance;
    }

    public String get(String key) {
        if (!this.enabled) return null;

        CacheItem item = cache.get(key);
        if (item == null) {
            return null;
        }

        if (item.timestamp().isBefore(Instant.now().minus(this.expirationTimeInMinutes, ChronoUnit.MINUTES))) {
            cache.remove(key);
            return null;
        } else {
            return item.value();
        }
    }

    public void put(String key, String value) {
        cache.put(key, new CacheItem(value, Instant.now()));
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    // Cache should be mocked
    @Deprecated
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}


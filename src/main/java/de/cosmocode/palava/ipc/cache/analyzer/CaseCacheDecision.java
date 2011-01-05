package de.cosmocode.palava.ipc.cache.analyzer;

import java.util.concurrent.TimeUnit;

import de.cosmocode.palava.ipc.cache.CacheDecision;

/**
 * A {@link CaseCached} based {@link CacheDecision}.
 *
 * @since 3.0
 * @author Willi Schoenborn
 */
final class CaseCacheDecision implements CacheDecision {
    
    private final boolean shouldCache;
    private final CaseCached annotation;

    CaseCacheDecision(boolean shouldCache, CaseCached annotation) {
        this.shouldCache = shouldCache;
        this.annotation = annotation;
    }

    @Override
    public boolean shouldCache() {
        return shouldCache;
    }

    @Override
    public long getLifeTime() {
        return annotation.lifeTime();
    }

    @Override
    public TimeUnit getLifeTimeUnit() {
        return annotation.lifeTimeUnit();
    }

    @Override
    public long getIdleTime() {
        return annotation.idleTime();
    }

    @Override
    public TimeUnit getIdleTimeUnit() {
        return annotation.idleTimeUnit();
    }
}

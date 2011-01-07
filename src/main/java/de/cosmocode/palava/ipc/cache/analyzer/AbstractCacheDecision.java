package de.cosmocode.palava.ipc.cache.analyzer;

import java.util.concurrent.TimeUnit;

import de.cosmocode.palava.ipc.cache.CacheDecision;

/**
 * Abstract {@link CacheDecision} implementation.
 *
 * @since 3.0
 * @author Willi Schoenborn
 */
public abstract class AbstractCacheDecision implements CacheDecision {

    @Override
    public boolean isEternal() {
        return getLifeTime() == 0L && getIdleTime() == 0L;
    }
    
    @Override
    public long getLifeTimeIn(TimeUnit unit) {
        return unit.convert(getLifeTime(), getLifeTimeUnit());
    }
    
    @Override
    public long getIdleTimeIn(TimeUnit unit) {
        return unit.convert(getIdleTime(), getIdleTimeUnit());
    }

    @Override
    public String toString() {
        return "CacheDecision [" +
                "shouldCache=" + shouldCache() + ", " +
                "lifeTime=" + getLifeTime() + ", " +
                "lifeTimeUnit=" + getLifeTimeUnit() + ", " +
                "idleTime=" + getIdleTime() + ", " +
                "idleTimeUnit=" + getIdleTimeUnit() + 
            "]";
    }
    
}

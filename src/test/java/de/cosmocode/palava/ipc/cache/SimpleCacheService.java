/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.ipc.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.cosmocode.palava.cache.CacheService;

/**
 * A simple {@link CacheService} that delegates its methods to a map.
 * 
 * @author Oliver Lorenz
 */
final class SimpleCacheService implements CacheService {
    
    private static final Logger LOG = LoggerFactory.getLogger(SimpleCacheService.class);
    
    private final Map<Serializable, Object> cache;
    private long maxAge = DEFAULT_MAX_AGE;
    private TimeUnit maxAgeUnit = DEFAULT_MAX_AGE_TIMEUNIT;
    
    public SimpleCacheService() {
        this.cache = Maps.newHashMap();
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(Serializable key) {
        Preconditions.checkNotNull(key, "Key");
        return (T) cache.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T remove(Serializable key) {
        Preconditions.checkNotNull(key, "Key");
        return (T) cache.remove(key);
    }

    @Override
    public void store(Serializable key, Object value) {
        this.store(key, value, maxAge, maxAgeUnit);
    }
    

    @Override
    public long getMaxAge() {
        return getMaxAge(TimeUnit.SECONDS);
    }
    
    @Override
    public long getMaxAge(TimeUnit unit) {
        return unit.convert(maxAge, maxAgeUnit);
    }
    
    @Override
    public void setMaxAge(long maxAgeSeconds) {
        this.setMaxAge(maxAgeSeconds, TimeUnit.SECONDS);
    }
    
    @Override
    public void setMaxAge(long newMaxAge, TimeUnit newMaxAgeUnit) {
        Preconditions.checkArgument(newMaxAge >= 0, MAX_AGE_NEGATIVE);
        Preconditions.checkNotNull(newMaxAgeUnit, "MaxAge TimeUnit");
        
        this.maxAge = newMaxAge;
        this.maxAgeUnit = newMaxAgeUnit;
    }
    
    @Override
    public void store(final Serializable key, final Object value, final long sMaxAge, final TimeUnit sMaxAgeUnit) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkArgument(sMaxAge >= 0, MAX_AGE_NEGATIVE);
        Preconditions.checkNotNull(sMaxAgeUnit, "MaxAge TimeUnit");
        
        cache.put(key, value);
        
        if (sMaxAge != DEFAULT_MAX_AGE && sMaxAgeUnit != DEFAULT_MAX_AGE_TIMEUNIT) {
            final Thread deleteThread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        Thread.sleep(TimeUnit.MILLISECONDS.convert(sMaxAge, sMaxAgeUnit));
                    } catch (InterruptedException e) {
                        LOG.warn("sleep was interrupted");
                    }
                    cache.remove(key);
                    LOG.debug("Item {} removed from cache after {} second(s)", 
                        key, TimeUnit.SECONDS.convert(sMaxAge, sMaxAgeUnit));
                };
                
            });
            deleteThread.start();
        }
    }

}

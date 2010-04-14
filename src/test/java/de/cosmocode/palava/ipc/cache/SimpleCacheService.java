/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

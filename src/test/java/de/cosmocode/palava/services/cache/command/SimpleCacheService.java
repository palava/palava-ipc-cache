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

package de.cosmocode.palava.services.cache.command;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

import de.cosmocode.palava.services.cache.CacheService;

/**
 * A simple {@link CacheService} that delegates its methods to a map.
 * 
 * @author Oliver Lorenz
 *
 */
final class SimpleCacheService implements CacheService {
    
    private final Map<Serializable, Object> cache;
    
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
        return (T) cache.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T remove(Serializable key) {
        return (T) cache.remove(key);
    }

    @Override
    public void store(Serializable key, Object value) {
        cache.put(key, value);
    }
    
    
    @Override
    public long getMaxAge() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long getMaxAge(TimeUnit unit) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMaxAge(long maxAge, TimeUnit maxAgeUnit) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMaxAge(long maxAgeSeconds) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void store(Serializable key, Object value, long maxAge, TimeUnit maxAgeUnit) {
        throw new UnsupportedOperationException();
    }

}

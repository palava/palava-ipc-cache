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

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * 
 *
 * @author Oliver Lorenz
 */
final class CustomCacheFilter implements IpcCallFilter {
    
    private final Provider<CacheFilter> filter;
    
    private CachePolicy policy = CachePolicy.SMART;
    
    private long maxAge;
    
    private TimeUnit maxAgeUnit = TimeUnit.MINUTES;
    
    private final Cache cache = new Cache() {
        
        @Override
        public Class<? extends Annotation> annotationType() {
            return Cache.class;
        }
        
        @Override
        public CachePolicy policy() {
            return policy;
        }
        
        @Override
        public long maxAge() {
            return maxAge;
        }
        
        @Override
        public TimeUnit maxAgeUnit() {
            return maxAgeUnit;
        }
        
    };
    
    @Inject
    public CustomCacheFilter(Provider<CacheFilter> filter) {
        this.filter = Preconditions.checkNotNull(filter, "Filter");
    }

    void setPolicy(CachePolicy policy) {
        this.policy = Preconditions.checkNotNull(policy, "Policy");
    }
    
    void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
    
    void setMaxAgeUnit(TimeUnit maxAgeUnit) {
        this.maxAgeUnit = Preconditions.checkNotNull(maxAgeUnit, "MaxAgeUnit");
    }
    
    @Override
    public Map<String, Object> filter(IpcCall call, IpcCommand command,
            IpcCallFilterChain chain) throws IpcCommandExecutionException {
        return filter.get().cache(call, command, chain, cache);
    }

}

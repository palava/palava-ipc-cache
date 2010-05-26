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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * A filter which allows custom caching for legacy commands
 * or commands which can't be annotated with {@link Cached}.
 *
 * @author Oliver Lorenz
 */
final class CustomCacheFilter implements IpcCallFilter {
    
    private final Provider<CommandCacheService> service;
    
    private CachePolicy policy = CachePolicy.SMART;
    
    private long maxAge;
    
    private TimeUnit maxAgeUnit = TimeUnit.MINUTES;
    
    public CustomCacheFilter(Provider<CommandCacheService> service) {
        this.service = Preconditions.checkNotNull(service, "Service");
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
        return service.get().cache(call, command, chain, policy, maxAge, maxAgeUnit);
    }

}

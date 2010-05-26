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

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * An {@link IpcCallFilter} which intercepts IpcCommand execution
 * to provide caching. This reduces repeated re-execution of the given IpcCommand.
 * 
 * @author Oliver Lorenz
 */
@Singleton
final class CacheFilter implements IpcCallFilter {
    
    private final CommandCacheService service;
    
    @Inject
    public CacheFilter(CommandCacheService service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }

    @Override
    public Map<String, Object> filter(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain)
        throws IpcCommandExecutionException {
        
        final Cached annotation = command.getClass().getAnnotation(Cached.class);
        assert annotation != null : String.format("Expected @%s to be present on %s", 
            Cached.class.getName(), command.getClass());
        
        return service.cache(call, command, chain, annotation.policy(), annotation.maxAge(), annotation.maxAgeUnit());
    }

}

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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
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
    private final Injector injector;
    
    @Inject
    public CacheFilter(CommandCacheService service, Injector injector) {
        this.service = Preconditions.checkNotNull(service, "Service");
        this.injector = Preconditions.checkNotNull(injector, "Injector");
    }

    @Override
    public Map<String, Object> filter(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain)
        throws IpcCommandExecutionException {
        
        final Cached annotation = command.getClass().getAnnotation(Cached.class);
        assert annotation != null : String.format("Expected @%s to be present on %s", 
            Cached.class.getSimpleName(), command.getClass());

        if (annotation.filters().length == 0) {
            // no filters: just cache it
            return service.cache(
                call, command, chain, annotation.policy(),
                annotation.maxAge(), annotation.maxAgeUnit());
        } else {
            // we have filters specified: create them via injector
            final List<Predicate<IpcCall>> filters;
            
            if (annotation.filters().length == 0) {
                filters = Collections.emptyList();
            } else {
                filters = Lists.newArrayListWithCapacity(annotation.filters().length);
                
                for (Class<Predicate<IpcCall>> predicateClass : annotation.filters()) {
                    filters.add(injector.getInstance(predicateClass));
                }
            }

            return service.cache(
                call, command, chain, annotation.policy(),
                annotation.maxAge(), annotation.maxAgeUnit(),
                filters, annotation.filterMode());
        }
    }

}

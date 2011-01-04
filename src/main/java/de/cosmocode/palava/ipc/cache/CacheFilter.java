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

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * An {@link IpcCallFilter} which intercepts IpcCommand execution
 * to provide caching. This reduces repeated re-execution of the given IpcCommand.
 * 
 * @author Oliver Lorenz
 */
@Singleton
final class CacheFilter implements IpcCallFilter {
    
    private final IpcCacheService service;
    private final Injector injector;
    
    @Inject
    public CacheFilter(IpcCacheService service, Injector injector) {
        this.service = Preconditions.checkNotNull(service, "Service");
        this.injector = Preconditions.checkNotNull(injector, "Injector");
    }

    @Override
    public Map<String, Object> filter(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain)
        throws IpcCommandExecutionException {

        // get annotations from command
        ComplexCacheAnnotation complexAnnotation = null;
        Annotation cacheAnnotation = null;
        for (Annotation annotation: command.getClass().getAnnotations()) {
            ComplexCacheAnnotation cca = annotation.getClass().getAnnotation(ComplexCacheAnnotation.class);
            if (cca != null) {
                Preconditions.checkState(complexAnnotation == null, "Multiple cache annotations found on %s", command.getClass().getName());
                cacheAnnotation = annotation;
                complexAnnotation = cca;
            }
        }
        Preconditions.checkState(complexAnnotation != null, "No cache annotation found on %s", command.getClass().getName());

        // already cached?
        Map<String,Object> result = service.getCachedResult(command, call);
        if (result != null) {
            return result;
        }

        // not cached, get the new result
        result = chain.filter(call, command);

        // check if we should cache it
        CacheAnalyzer cacheAnalyzer = injector.getInstance(complexAnnotation.analyzer());
        CacheDecision decision = cacheAnalyzer.analyze(cacheAnnotation, call, command);

        if (decision.shouldCache()) {
            service.setCachedResult(command, call, decision, result);
        }

        return result;
    }

}

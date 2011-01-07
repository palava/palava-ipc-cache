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

import com.google.common.base.Preconditions;
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
 * <p>
 * To get your annotation processed by the filter extend the {@link AbstractCacheModule} and
 * call {@link AbstractCacheModule#use(Class)} with your annotation.
 * </p>
 * 
 * @author Oliver Lorenz
 * @author Tobias Sarnowski
 * @since 1.0
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
        IpcCall call, IpcCommand command, IpcCallFilterChain chain)
        throws IpcCommandExecutionException {

        final Annotation cacheAnnotation = checkAndGetAnnotation(command);
        final Class<? extends Annotation> annotationType = cacheAnnotation.annotationType();
        final ComplexCacheAnnotation complexAnnotation = annotationType.getAnnotation(ComplexCacheAnnotation.class);
        
        final Map<String, Object> cached = service.read(command, call);
        
        if (cached == null) {
            final CacheAnalyzer analyzer = injector.getInstance(complexAnnotation.analyzer());
            final CacheDecision decision = analyzer.analyze(cacheAnnotation, call, command);
            final IpcCommandExecution computation = new IpcFilterChainExecution(call, command, chain);
            return service.computeAndStore(command, call, decision, computation);
        } else {
            return cached;
        }
    }
    
    private Annotation checkAndGetAnnotation(IpcCommand command) {
        Annotation found = null;
        for (Annotation annotation : command.getClass().getAnnotations()) {
            if (annotation.getClass().isAnnotationPresent(ComplexCacheAnnotation.class)) {
                Preconditions.checkState(found == null, "Multiple cache annotations found on %s", command.getClass());
                found = annotation;
            }
        }
        Preconditions.checkState(found != null, "No cache annotation found on %s", command.getClass());
        return found;
    }

}

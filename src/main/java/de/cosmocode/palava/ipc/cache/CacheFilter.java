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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.cache.CacheService;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * <p> An {@link IpcCallFilter} which intercepts IpcCommand execution
 * to provide caching. This reduces repeated re-execution of the given IpcCommand.
 * </p>
 * 
 * @author Oliver Lorenz
 */
@Singleton
final class CacheFilter implements IpcCallFilter, CacheKeyFactory {
    
    public static final String ERR_STATIC_ARGUMENTS =
        "Illegally provided arguments for static cached command";
    

    private static final Logger LOG = LoggerFactory.getLogger(IpcCallFilter.class);

    private final CacheService service;
    
    private CacheKeyFactory keyFactory = this;
    
    @Inject
    CacheFilter(@CommandCache CacheService service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }

    @Inject(optional = true)
    void setKeyFactory(CacheKeyFactory keyFactory) {
        this.keyFactory = Preconditions.checkNotNull(keyFactory, "KeyFactory");
    }

    @Override
    public Map<String, Object> filter(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain)
        throws IpcCommandExecutionException {
        
        final Cache annotation = command.getClass().getAnnotation(Cache.class);
        assert annotation != null : String.format("Expected @%s to be present on %s", 
            Cache.class.getName(), command.getClass());
        
        switch (annotation.policy()) {
            case STATIC: {
                return cacheStatic(call, command, chain, annotation);
            }
            case PINKY: {
                return cachePinky(call, command, chain, annotation);
            }
            case SMART: {
                return cacheSmart(call, command, chain, annotation);
            }
            default: {
                throw new IllegalArgumentException("Unknown cache policy " + annotation.policy());
            }
        }
    }
    
    Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain,
        Cache annotation) throws IpcCommandExecutionException {
        Preconditions.checkNotNull(call, "Call");
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(chain, "Chain");
        Preconditions.checkNotNull(annotation, "Annotation");
        
        switch (annotation.policy()) {
            case STATIC: {
                return cacheStatic(call, command, chain, annotation);
            }
            case PINKY: {
                return cachePinky(call, command, chain, annotation);
            }
            case SMART: {
                return cacheSmart(call, command, chain, annotation);
            }
            default: {
                throw new IllegalArgumentException("Unknown cache policy " + annotation.policy());
            }
        }
    }
    
    private Map<String, Object> cacheStatic(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain,
        final Cache annotation) throws IpcCommandExecutionException {
        
        Preconditions.checkState(call.getArguments().isEmpty(), ERR_STATIC_ARGUMENTS);
        
        final Class<?> type = command.getClass();
        final Map<String, Object> cached = service.read(type);
        if (cached == null) {
            LOG.debug("Caching content from {} statically", type);
            final Map<String, Object> content = chain.filter(call, command);
            if (annotation.maxAge() > 0) {
                service.store(type, content, annotation.maxAge(), annotation.maxAgeUnit());
            } else {
                service.store(type, content);
            }
            return content;
        } else {
            LOG.trace("Found statically cached content for {}", type);
            return cached;
        }
    }
    
    private Map<String, Object> cachePinky(IpcCall call, IpcCommand command,
        IpcCallFilterChain chain, Cache annotation) throws IpcCommandExecutionException {
        
        final Serializable key = create(call, command);
        final Map<String, Object> cached = service.read(key);

        if (cached == null) {
            LOG.trace("Caching content pinky with key {}", key);
            final Map<String, Object> content = chain.filter(call, command);
            if (annotation.maxAge() > 0) {
                service.store(key, content, annotation.maxAge(), annotation.maxAgeUnit());
            } else {
                service.store(key, content);
            }
            return content;
        } else {
            LOG.trace("Found cached content for {}", key);
            return cached;
        }
    }
    
    private Map<String, Object> cacheSmart(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain,
        final Cache annotation) throws IpcCommandExecutionException {
        
        final Serializable key = keyFactory.create(call, command);
        final Map<String, Object> cached = service.read(key);

        if (cached == null) {
            LOG.trace("Caching content smart with key {}", key);
            final Map<String, Object> content = chain.filter(call, command);
            if (annotation.maxAge() > 0) {
                service.store(key, content, annotation.maxAge(), annotation.maxAgeUnit());
            } else {
                service.store(key, content);
            }
            return content;
        } else {
            LOG.trace("Found cached content for {}", key);
            return cached;
        }
    }
    
    @Override
    public Serializable create(IpcCall call, IpcCommand command) {
        final IpcArguments arguments = call.getArguments();
        if (arguments.isEmpty()) {
            return command.getClass();
        } else {
            return ImmutableSet.of(command.getClass(), call.getArguments());
        }
    }

}

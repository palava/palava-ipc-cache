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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

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
 * FIXME scope keys can only be configured as strings, but can be objects 
 * 
 * @author Oliver Lorenz
 */
final class CacheFilter implements IpcCallFilter {
    
    public static final String ERR_STATIC_ARGUMENTS =
        "Illegally provided arguments for static cached command";

    private static final Logger LOG = LoggerFactory.getLogger(IpcCallFilter.class);
    
    private static final Splitter SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

    private final CacheService service;
    
    private final Set<String> callScopeKeys = Sets.newHashSet();
    
    private final Set<String> connectionScopeKeys = Sets.newHashSet();
    
    private final Set<String> sessionScopeKeys = Sets.newHashSet();
    
    /**
     * <p> This boolean only exists to increase performance.
     * If no scope keys have been set, then this is false,
     * otherwise (if any scope keys have been set) this is true.
     * </p>
     * <p> The following methods are scope methods.
     * Their successful invocation (either through injection or a direct method call)
     * changes this boolean to true: </p>
     * <ul>
     *   <li> {@link #setCallScopeKeys(String)} </li>
     *   <li> {@link #setConnectionScopeKeys(String)} </li>
     *   <li> {@link #setSessionScopeKeys(String)} </li>
     * </ul>
     */
    private boolean isScopeAware;
    
    
    @Inject
    public CacheFilter(final CacheService service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }
    
    
    /**
     * <p> Splits the given String parameter on "," and puts them into a {@code Set<String>}
     * that is evaluated in smart caching mode.
     * </p>
     * <p> The context keys must be separated by commas, but can contain blanks
     * and even blank Strings. Blanks and empty Strings are filtered out.
     * </p>
     * 
     * @param context the context to put the new keys into
     * @param keys the new context keys, as String, separated by ","
     */
    private static void splitString(final Set<String> context, final String keys) {
        context.clear();
        for (String key : SPLITTER.split(keys)) {
            context.add(key);
        }
    }
    
    /**
     * Sets the call scope keys.
     * The corresponding objects are considered for caching in {@link CachePolicy#SMART}.
     * @param keys the call scope keys, as a String, separated by commas
     */
    @Inject(optional = true)
    public void setCallScopeKeys(@Named("command.cache.keys.call") final String keys) {
        splitString(callScopeKeys, keys);
        if (callScopeKeys.size() > 0) isScopeAware = true;
    }
    
    /**
     * Sets the connection scope keys.
     * The corresponding objects are considered for caching in {@link CachePolicy#SMART}.
     * @param keys the connection scope keys, as a String, separated by commas
     */
    @Inject(optional = true)
    public void setConnectionScopeKeys(@Named("command.cache.keys.connection") final String keys) {
        splitString(connectionScopeKeys, keys);
        if (connectionScopeKeys.size() > 0) isScopeAware = true;
    }
    
    /**
     * Sets the session scope keys.
     * The corresponding objects are considered for caching in {@link CachePolicy#SMART}.
     * @param keys the session scope keys, as a String, separated by commas
     */
    @Inject(optional = true)
    public void setSessionScopeKeys(@Named("command.cache.keys.session") final String keys) {
        splitString(sessionScopeKeys, keys);
        if (sessionScopeKeys.size() > 0) isScopeAware = true;
    }
    

    @Override
    public Map<String, Object> filter(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain)
        throws IpcCommandExecutionException {
        
        final Cache annotation = command.getClass().getAnnotation(Cache.class);
        
        switch (annotation.policy()) {
            case STATIC: {
                return cacheStatic(call, command, chain, annotation);
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
    
    private Map<String, Object> cacheSmart(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain,
        final Cache annotation) throws IpcCommandExecutionException {
        
        final Class<?> commandClass = command.getClass();
        final IpcArguments arguments = call.getArguments();
        final Set<Object> scopeRelevant;
        
        if (isScopeAware) {
            scopeRelevant = getScopeRelevant(call);
        } else {
            scopeRelevant = ImmutableSet.of();
        }
        
        final Serializable cacheItem;
        final Map<String, Object> cached;
        
        // determine the cache item, based on arguments
        if (arguments.isEmpty()) {
            cacheItem = ImmutableSet.of(commandClass, scopeRelevant);
        } else {
            cacheItem = ImmutableSet.of(commandClass, arguments, scopeRelevant);
        }
        
        cached = service.read(cacheItem);

        if (cached == null) {
            LOG.trace("Caching content from {} smart with CacheItem {}", commandClass, cacheItem);
            final Map<String, Object> content = chain.filter(call, command);
            if (annotation.maxAge() > 0) {
                service.store(cacheItem, content, annotation.maxAge(), annotation.maxAgeUnit());
            } else {
                service.store(cacheItem, content);
            }
            return content;
        } else {
            LOG.trace("Found cached content for {} (CacheItem: {})", commandClass, cacheItem);
            return cached;
        }
    }
    
    private Set<Object> getScopeRelevant(final IpcCall call) {
        final Set<Object> scopeRelevant = Sets.newHashSet();

        // look in call scope
        for (final String contextKey : callScopeKeys) {
            final Object obj = call.get(contextKey);
            if (obj == null) continue;
            scopeRelevant.add(obj);
        }
        
        // look in connection scope
        for (final String contextKey : connectionScopeKeys) {
            final Object obj = call.getConnection().get(contextKey);
            if (obj == null) continue;
            scopeRelevant.add(obj);
        }
        
        // look in session scope
        for (final String contextKey : sessionScopeKeys) {
            final Object obj = call.getConnection().getSession().get(contextKey);
            if (obj == null) continue;
            scopeRelevant.add(obj);
        }
        
        return scopeRelevant;
    }

}

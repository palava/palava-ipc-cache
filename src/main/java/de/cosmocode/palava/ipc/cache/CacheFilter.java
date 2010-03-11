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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
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
 * @author Oliver Lorenz
 */
final class CacheFilter implements IpcCallFilter {

    private static final Logger LOG = LoggerFactory.getLogger(IpcCallFilter.class);

    private final CacheService service;
    
    private final Set<String> callScopeKeys = Sets.newHashSet();
    
    private final Set<String> connectionScopeKeys = Sets.newHashSet();
    
    private final Set<String> sessionScopeKeys = Sets.newHashSet();
    
    
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
    private void splitString(final Set<String> context, final String keys) {
        context.clear();
        final String[] tmpVars = Preconditions.checkNotNull(keys, "Keys").split(",");
        for (final String scopeVar : tmpVars) {
            if (StringUtils.isBlank(scopeVar)) continue;
            context.add(scopeVar.trim());
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
    }
    
    /**
     * Sets the connection scope keys.
     * The corresponding objects are considered for caching in {@link CachePolicy#SMART}.
     * @param keys the connection scope keys, as a String, separated by commas
     */
    @Inject(optional = true)
    public void setConnectionScopeKeys(@Named("command.cache.keys.connection") final String keys) {
        splitString(connectionScopeKeys, keys);
    }
    
    /**
     * Sets the session scope keys.
     * The corresponding objects are considered for caching in {@link CachePolicy#SMART}.
     * @param keys the session scope keys, as a String, separated by commas
     */
    @Inject(optional = true)
    public void setSessionScopeKeys(@Named("command.cache.keys.session") final String keys) {
        splitString(sessionScopeKeys, keys);
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
        
        final Class<?> type = command.getClass();
        final Map<String, Object> cached = service.read(type);
        if (cached == null) {
            final Map<String, Object> content = chain.filter(call, command);
            LOG.debug("Caching content from {} statically", type);
            final long maxAge = annotation.maxAge();
            if (maxAge > 0) {
                service.store(type, content, maxAge, annotation.maxAgeUnit());
            } else {
                service.store(type, content);
            }
            return content;
        } else {
            LOG.debug("Found statically cached content for {}", type);
            return cached;
        }
    }
    
    private Map<String, Object> cacheSmart(
        final IpcCall call, final IpcCommand command, final IpcCallFilterChain chain,
        final Cache annotation) throws IpcCommandExecutionException {
        
        final Class<?> commandClass = command.getClass();
        final IpcArguments arguments = call.getArguments();
        final Set<Object> scopeRelevant = getScopeRelevant(call);
        
        final Serializable cacheItem;
        final Map<String, Object> cached;
        
        // determine the cache item, based on arguments
        if (arguments == null) {
            cacheItem = ImmutableSet.of(commandClass, scopeRelevant);
        } else {
            cacheItem = ImmutableSet.of(commandClass, arguments, scopeRelevant);
        }
        
        cached = service.read(cacheItem);

        if (cached == null) {
            final Map<String, Object> content = chain.filter(call, command);
            LOG.debug("Caching content from {} smart with CacheItem {}", commandClass, cacheItem);
            final long maxAge = annotation.maxAge();
            if (maxAge > 0) {
                service.store(cacheItem, content, maxAge, annotation.maxAgeUnit());
            } else {
                service.store(cacheItem, content);
            }
            return content;
        } else {
            LOG.debug("Found cached content for {} (CacheItem: {})", commandClass, cacheItem);
            return cached;
        }
    }
    
    private Set<Object> getScopeRelevant(final IpcCall call) {
        final Set<Object> scopeRelevant = new HashSet<Object>();

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

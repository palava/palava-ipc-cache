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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import de.cosmocode.palava.cache.CacheService;
import de.cosmocode.palava.ipc.Ipc;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * Default {@link CommandCacheService} implementation.
 *
 * @since 1.0
 * @author Willi Schoenborn
 * @author Tobias Sarnowski
 */
final class DefaultCommandCacheService implements CommandCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCommandCacheService.class);

    private final CacheService service;

    private CacheKeyFactory factory;
    
    @Inject
    public DefaultCommandCacheService(@Ipc CacheService service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }
    
    @Override
    @Inject(optional = true)
    public void setFactory(CacheKeyFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Override
    public Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain, CachePolicy policy)
        throws IpcCommandExecutionException {
        
        Preconditions.checkNotNull(call, "Call");
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(chain, "Chain");
        Preconditions.checkNotNull(policy, "Policy");
        
        final CacheKey key = create(call, command, policy);
        final Map<String, Object> cached = service.read(key);
        
        if (cached == null) {
            final Map<String, Object> content = chain.filter(call, command);
            LOG.trace("Caching content for {} using policy {}", command, policy);
            service.store(key, content);
            updateIndex(command, key);
            return content;
        } else {
            return cached;
        }
    }

    @Override
    public Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain, CachePolicy policy,
        long maxAge, TimeUnit maxAgeUnit) throws IpcCommandExecutionException {
        
        Preconditions.checkNotNull(call, "Call");
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(chain, "Chain");
        Preconditions.checkNotNull(policy, "Policy");
        Preconditions.checkArgument(maxAge >= 0, "MaxAge must not be negative");
        Preconditions.checkNotNull(maxAgeUnit, "MaxAgeUnit");

        if (maxAge == 0) {
            return cache(call, command, chain, policy);
        } else {
            final CacheKey key = create(call, command, policy);
            final Map<String, Object> cached = service.read(key);
            
            if (cached == null) {
                final Map<String, Object> content = chain.filter(call, command);
                LOG.trace("Caching content for {} using policy {}", command, policy);
                service.store(key, content, maxAge, maxAgeUnit);
                updateIndex(command, key);
                return content;
            } else {
                return cached;
            }
        }
    }

    private CacheKey create(IpcCall call, IpcCommand command, CachePolicy policy) {
        if (policy == CachePolicy.SMART && factory != null) {
            return factory.create(call, command);
        } else {
            return policy.create(call, command);
        }
    }

    private void updateIndex(IpcCommand command, CacheKey cacheKey) {
        final Class<? extends IpcCommand> type = command.getClass();
        final IndexKey indexKey = IndexKey.create(type);
        Set<CacheKey> index = service.read(indexKey);

        if (index == null) {
            index = Sets.newHashSet();
        }

        index.add(cacheKey);
        service.store(indexKey, index);
    }

    @Override
    public void invalidate(Class<? extends IpcCommand> command) {
        invalidate(command, Predicates.alwaysTrue());
    }

    @Override
    public void invalidate(Class<? extends IpcCommand> command, Predicate<? super CacheKey> predicate) {
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(predicate, "Predicate");

        final IndexKey indexKey = IndexKey.create(command);
        final Set<CacheKey> index = service.read(indexKey);
        
        if (index == null) {
            LOG.trace("No cached versions of {} found.", command);
        } else {
            LOG.trace("Trying to invalidate {} cached versions of {}...", index.size(), command);
            
            final Iterator<CacheKey> iterator = index.iterator();
            
            while (iterator.hasNext()) {
                final CacheKey cacheKey = iterator.next();
                if (predicate.apply(cacheKey)) {
                    LOG.trace("{} matches {}, invalidating...", cacheKey, predicate);
                    service.remove(cacheKey);
                    iterator.remove();
                } else {
                    LOG.trace("{} does not match {}", cacheKey, predicate);
                }
            }
            
            if (index.isEmpty()) {
                LOG.trace("Removing empty index for {}", command);
                service.remove(indexKey);
            } else {
                LOG.trace("Updating index for {}", command);
                service.store(indexKey, index);
            }
        }
    }

}

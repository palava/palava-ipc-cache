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
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import de.cosmocode.palava.cache.CacheService;
import de.cosmocode.palava.ipc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Default {@link CommandCacheService} implementation.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class DefaultCommandCacheService implements CommandCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCommandCacheService.class);

    private final CacheService service;

    private CacheKeyFactory factory;
    
    @Inject
    public DefaultCommandCacheService(@Ipc CacheService service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }
    
    @Inject(optional = true)
    @Override
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
            indexCachedCommand(command.getClass(), key);
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
                indexCachedCommand(command.getClass(), key);
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

    private void indexCachedCommand(Class<? extends IpcCommand> command, CacheKey cacheKey) {
        IndexKey indexKey = IndexKey.create(command);
        IndexList indexList = service.read(indexKey);

        if (indexList == null) {
            indexList = new IndexList();
        }

        indexList.add(cacheKey);
        service.store(indexKey, indexList);
    }

    /**
     * @param command the IpcCommand definition to invalidate
     */
    @Override
    public void invalidate(Class<? extends IpcCommand> command) {
        invalidate(command, new Predicate<CacheKey>() {
            @Override
            public boolean apply(@Nullable CacheKey input) {
                return true;
            }
        });
    }

    @Override
    public void invalidate(Class<? extends IpcCommand> command, Predicate<CacheKey> predicate) {
        Serializable indexKey = IndexKey.create(command);

        IndexList indexList = service.read(indexKey);
        service.remove(indexKey);

        if (indexList != null) {
            LOG.trace("Trying to invalidate {} cached versions of {}...", indexList.size(), command.getClass());

            for (CacheKey cacheKey: indexList) {

                if (predicate.apply(cacheKey)) {
                    LOG.trace("{} matches predicate; invalidating");
                    service.remove(cacheKey);
                } else {
                    LOG.trace("{} does not match predicate");
                }

            }
        } else {
            LOG.trace("No cached versions of {} found.", command.getClass());
        }
    }

}

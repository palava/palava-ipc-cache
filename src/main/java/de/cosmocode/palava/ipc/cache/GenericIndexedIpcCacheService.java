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
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import de.cosmocode.palava.cache.CacheService;
import de.cosmocode.palava.ipc.Ipc;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Binds an {@link IpcCacheService} to an implementation which uses a {@link de.cosmocode.palava.cache.CacheService}
 * and requires the {@link de.cosmocode.palava.cache.CacheService} to be bound annotated with
 * {@link de.cosmocode.palava.ipc.Ipc}.
 *
 * @see GenericIndexedIpcCacheServiceModule
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 */
class GenericIndexedIpcCacheService extends AbstractIpcCacheService {
    private static final Logger LOG = LoggerFactory.getLogger(GenericIndexedIpcCacheService.class);

    private final CacheService cacheService;

    @Inject
    GenericIndexedIpcCacheService(@Ipc CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public Map<String, Object> getCachedResult(IpcCommand command, IpcCall call) {
        return cacheService.read(create(call, command));
    }

    @Override
    public void setCachedResult(IpcCommand command, IpcCall call, CacheDecision decision, Map<String, Object> result) {
        final CacheKey key = create(call, command);
        if (decision.getLifeTime() == 0) {
            cacheService.store(key, result);
            LOG.trace("Caching content for {} with unlimited time to live", command);
        } else {
            cacheService.store(key, result, decision.getLifeTime(), decision.getLifeTimeUnit());
            LOG.trace("Caching content for {} with life time", command);
            LOG.trace("Time to live is {} {}", decision.getLifeTime(), decision.getLifeTimeUnit());
        }
        addToIndex(command, key);
    }

    private void addToIndex(IpcCommand command, CacheKey cacheKey) {
        final Class<? extends IpcCommand> type = command.getClass();
        final IndexKey indexKey = IndexKey.create(type);
        Set<CacheKey> index = cacheService.read(indexKey);

        if (index == null) {
            index = Sets.newHashSet();
        }

        index.add(cacheKey);
        cacheService.store(indexKey, index);
    }

    @Override
    public void invalidate(Class<? extends IpcCommand> command, Predicate<? super CacheKey> predicate) {
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(predicate, "Predicate");

        final IndexKey indexKey = IndexKey.create(command);
        final Set<CacheKey> index = cacheService.read(indexKey);

        if (index == null) {
            LOG.trace("No cached versions of {} found.", command);
        } else {
            LOG.trace("Trying to invalidate {} cached versions of {}...", index.size(), command);

            final Iterator<CacheKey> iterator = index.iterator();

            while (iterator.hasNext()) {
                final CacheKey cacheKey = iterator.next();
                if (predicate.apply(cacheKey)) {
                    LOG.trace("{} matches {}, invalidating...", cacheKey, predicate);
                    cacheService.remove(cacheKey);
                    iterator.remove();
                } else {
                    LOG.trace("{} does not match {}", cacheKey, predicate);
                }
            }

            if (index.isEmpty()) {
                LOG.trace("Removing empty index for {}", command);
                cacheService.remove(indexKey);
            } else {
                LOG.trace("Updating index for {}", command);
                cacheService.store(indexKey, index);
            }
        }
    }
    
}

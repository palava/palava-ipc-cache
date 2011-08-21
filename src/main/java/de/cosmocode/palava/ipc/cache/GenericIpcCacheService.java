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
import de.cosmocode.palava.cache.CacheExpiration;
import de.cosmocode.palava.cache.CacheService;
import de.cosmocode.palava.cache.ComputingCacheService;
import de.cosmocode.palava.ipc.Ipc;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Binds an {@link IpcCacheService} to an implementation which uses a {@link CacheService}
 * and requires the {@link CacheService} to be bound annotated with {@link Ipc}.
 *
 * @see GenericIpcCacheServiceModule
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 */
final class GenericIpcCacheService extends AbstractIpcCacheService {
    
    private final ComputingCacheService service;

    @Inject
    GenericIpcCacheService(@Ipc ComputingCacheService cacheService) {
        this.service = Preconditions.checkNotNull(cacheService, "ComputingCacheService");
    }

    @Override
    public Map<String, Object> read(CacheKey key) {
        Preconditions.checkNotNull(key, "CacheKey");
        return service.read(key);
    }

    @Override
    public Map<String, Object> computeAndStore(CacheKey key, CacheExpiration expiration,
            IpcCommandExecution computation) throws IpcCommandExecutionException {
        
        Preconditions.checkNotNull(key, "CacheKey");
        Preconditions.checkNotNull(expiration, "Decision");
        Preconditions.checkNotNull(computation, "Computation");

        try {
            return service.computeAndStore(key, computation, expiration);
        } catch (ExecutionException e) {
            throw new IpcCommandExecutionException(e.getCause());
        }
    }
    
    @Override
    public void invalidate(Class<? extends IpcCommand> command, Predicate<? super CacheKey> predicate) {
        throw new UnsupportedOperationException();
    }
    
}

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


import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

import de.cosmocode.palava.cache.CacheService;
import de.cosmocode.palava.cache.ComputingCacheService;
import de.cosmocode.palava.ipc.Ipc;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

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
    
    private static final Logger LOG = LoggerFactory.getLogger(GenericIpcCacheService.class);

    private final CacheService cacheService;
    private ComputingCacheService computingCacheService;

    @Inject
    GenericIpcCacheService(@Ipc CacheService cacheService) {
        this.cacheService = cacheService;
    }
    
    @Inject(optional = true)
    void setComputingCacheService(@Ipc ComputingCacheService computingCacheService) {
        this.computingCacheService = Preconditions.checkNotNull(computingCacheService, "ComputingCacheService");
    }

    @Override
    public Map<String, Object> read(IpcCommand command, IpcCall call) {
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(call, "Call");
        return cacheService.read(create(call, command));
    }

    @Override
    public Map<String, Object> computeAndStore(IpcCommand command, IpcCall call, CacheDecision decision,
            IpcCommandExecution computation) throws IpcCommandExecutionException {
        
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(call, "Call");
        Preconditions.checkNotNull(decision, "Decision");
        Preconditions.checkNotNull(computation, "Computation");

        if (decision.shouldCache()) {
            final CacheKey key = create(call, command);
            if (computingCacheService == null) {
                return computeAndDelegateStore(key, computation, decision);
            } else {
                return delegateComputeAndStore(key, computation, decision);
            }
        } else {
            return compute(computation);
        }
    }
    
    private Map<String, Object> compute(IpcCommandExecution computation) throws IpcCommandExecutionException {
        LOG.trace("Suppressing caching of {}", computation);
        return computation.call();
    }
    
    private Map<String, Object> computeAndDelegateStore(CacheKey key, IpcCommandExecution computation, 
            CacheDecision decision) throws IpcCommandExecutionException {
        
        final Map<String, Object> result = computation.call();
        final long maxAge = decision.getLifeTime();
        
        if (maxAge == 0) {
            cacheService.store(key, result);
        } else {
            final TimeUnit maxAgeUnit = decision.getLifeTimeUnit();
            cacheService.store(key, result, maxAge, maxAgeUnit);
        }
        
        return result;
    }
    
    private Map<String, Object> delegateComputeAndStore(CacheKey key, IpcCommandExecution computation, 
            CacheDecision decision) throws IpcCommandExecutionException {
        
        final long maxAge = decision.getLifeTime();
        
        if (maxAge == 0) {
            try {
                return computingCacheService.computeAndStore(key, computation);
            } catch (ExecutionException e) {
                throw new IpcCommandExecutionException(e.getCause());
            }
        } else {
            final TimeUnit maxAgeUnit = decision.getLifeTimeUnit();
            try {
                return computingCacheService.computeAndStore(key, computation, maxAge, maxAgeUnit);
            } catch (ExecutionException e) {
                throw new IpcCommandExecutionException(e.getCause());
            }
        }
    }

    @Override
    public void invalidate(Class<? extends IpcCommand> command, Predicate<? super CacheKey> predicate) {
        throw new UnsupportedOperationException();
    }
    
}

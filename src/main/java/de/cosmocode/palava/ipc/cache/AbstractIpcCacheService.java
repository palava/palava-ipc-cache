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
import com.google.common.base.Predicates;
import com.google.inject.Inject;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * Utility class to perform some basic and trivial tasks. Implementations have to use
 * {@link de.cosmocode.palava.ipc.cache.AbstractIpcCacheService#getCacheKeyFactory()} to get a serializable key for
 * their cache.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 */
public abstract class AbstractIpcCacheService implements IpcCacheService, CacheKeyFactory {

    private CacheKeyFactory cacheKeyFactory = DefaultCacheKeyFactory.INSTANCE;

    /**
     * The configured {@link CacheKeyFactory} for your service.
     *
     * @return the configured factory
     */
    public CacheKeyFactory getCacheKeyFactory() {
        return cacheKeyFactory;
    }

    /**
     * Optional possibility to change the default {@link CacheKeyFactory} with another implementation.
     *
     * @param cacheKeyFactory the new factory to use
     */
    @Inject(optional = true)
    public void setCacheKeyFactory(CacheKeyFactory cacheKeyFactory) {
        this.cacheKeyFactory = Preconditions.checkNotNull(cacheKeyFactory, "CacheKeyFactory");
    }

    @Override
    public CacheKey create(IpcCall call, IpcCommand command) {
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(call, "Call");
        return getCacheKeyFactory().create(call, command);
    }

    @Override
    public void invalidate(Class<? extends IpcCommand> command) {
        Preconditions.checkNotNull(command, "Command");
        invalidate(command, Predicates.alwaysTrue());
    }

}

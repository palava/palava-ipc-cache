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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * Binds an {@link IpcCacheService} to an implementation which uses a {@link de.cosmocode.palava.cache.CacheService}
 * and requires the {@link de.cosmocode.palava.cache.CacheService} to be bound annotated with
 * {@link de.cosmocode.palava.ipc.Ipc}.
 *
 * <p>
 * <strong>Note:</strong> The resulting {@link IpcCacheService} does support {@link IpcCacheService#invalidate(Class)}
 * and {@link IpcCacheService#invalidate(Class, com.google.common.base.Predicate)} <strong>but is not recommended for
 * production use!</strong>
 * </p>
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 */
public final class GenericIndexedIpcCacheServiceModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(IpcCacheService.class).to(GenericIndexedIpcCacheService.class).in(Singleton.class);
    }
}

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

import com.google.inject.Singleton;

import de.cosmocode.palava.ipc.Commands;
import de.cosmocode.palava.ipc.FilterModule;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * <p> Module that enables caching for {@link IpcCommand}s.
 * Cached commands are identified by an {@code @Cache} annotation.
 * </p>
 * <p> Caching can either be done {@linkplain CachePolicy#STATIC static}
 * or {@linkplain CachePolicy#SMART smart}.
 * </p>
 * 
 * @author Willi Schoenborn
 * @author Oliver Lorenz
 */
public final class CacheFilterModule extends FilterModule {
    
    /**
     * <p> Module that enables caching for {@link IpcCommand}s.
     * Cached commands are identified by an {@code @Cache} annotation.
     * </p>
     * <p> Caching can either be done {@linkplain CachePolicy#STATIC static}
     * or {@linkplain CachePolicy#SMART smart}.
     * </p>
     */
    public CacheFilterModule() {
        
    }

    @Override
    protected void configure() {
        filter(Commands.annotatedWith(Cache.class)).through(CacheFilter.class);
        bind(CommandCacheService.class).to(DefaultCommandCacheService.class).in(Singleton.class);
    }
    
}
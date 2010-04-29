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

import de.cosmocode.palava.ipc.Commands;
import de.cosmocode.palava.ipc.FilterModule;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcConnection;
import de.cosmocode.palava.ipc.IpcSession;

/**
 * <p> Module that enables caching for {@link IpcCommand}s.
 * Cached commands are identified by an {@code @Cache} annotation.
 * </p>
 * <p> Caching can either be done {@linkplain CachePolicy#STATIC static}
 * or {@linkplain CachePolicy#SMART smart}.
 * </p>
 * <p> Optional configuration properties for the command cache are:
 * </p>
 * <dl>
 *   <dt> command.cache.keys.call
 *   <dd> a comma separated list of Strings,
 *        that correspond to the keys in the call scope
 *        ({@link IpcCall#get(Object)})
 *   <dt> command.cache.keys.connection
 *   <dd> a comma separated list of Strings,
 *        that correspond to the keys in the connection scope
 *        ({@link IpcConnection#get(Object)})
 *   <dt> command.cache.keys.session
 *   <dd> a comma separated list of Strings,
 *        that correspond to the keys in the call scope
 *        ({@link IpcSession#get(Object)})
 * </dl>
 * 
 * @author Willi Schoenborn
 * @author Oliver Lorenz
 */
public class CommandCacheModule extends FilterModule {

    @Override
    protected void configure() {
        filter(Commands.annotatedWith(Cache.class)).through(CacheFilter.class);
    }
    
}

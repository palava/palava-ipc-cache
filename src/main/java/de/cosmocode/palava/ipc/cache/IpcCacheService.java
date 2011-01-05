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

import com.google.common.base.Predicate;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

import java.util.Map;

/**
 * This interface defines the core implementation for caching {@link IpcCommand} results.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 */
public interface IpcCacheService {

    /**
     * Returns the cached value based on the input. If no value exists, returns null.
     *
     * @param command the called command
     * @param call the IPC call
     * @return cached result or null
     */
    Map<String, Object> getCachedResult(IpcCommand command, IpcCall call);

    /**
     * Caches the given result for the given command call with a decision how the result should be cached.
     *
     * @param command the called command
     * @param call the IPC call
     * @param decision the decision how to cache
     * @param result the result to cache
     */
    void setCachedResult(IpcCommand command, IpcCall call, CacheDecision decision, Map<String, Object> result);

    /**
     * Invalidates all cached versions of an {@link IpcCommand}.
     *
     * @since 2.1
     * @param command the IpcCommand definition to invalidate
     * @throws NullPointerException if command is null
     */
    void invalidate(Class<? extends IpcCommand> command);

    /**
     * Invalidates cached versions of an {@link IpcCommand} matching a predicate.
     *
     * @since 2.1
     * @param command the IpcCommand definition to invalidate
     * @param predicate a predicate to only invalidate self-filtered entries
     * @throws NullPointerException if command is null or predicate is null
     */
    void invalidate(Class<? extends IpcCommand> command, Predicate<? super CacheKey> predicate);

}

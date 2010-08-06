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
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * A service which encapsulates caching {@link IpcCommand} results
 * produced by {@link IpcCommand#execute(IpcCall, Map)}.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public interface CommandCacheService {

    /**
     * Sets the {@link CacheKeyFactory} of this service.
     * 
     * @since 2.0
     * @param factory the new factory
     * @throws NullPointerException if factory is null
     */
    void setFactory(CacheKeyFactory factory);
    
    /**
     * Eventually caches the content produces by the given chain or returns an already
     * cached version.
     * 
     * @since 2.0
     * @param call the incoming call
     * @param command the command being used to execute
     * @param chain the filter chain
     * @param policy the cache policy
     * @return the cached content
     * @throws IpcCommandExecutionException if chain execution failed
     */
    Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain,
        CachePolicy policy) throws IpcCommandExecutionException;
    
    /**
     * Eventually caches the content produces by the given chain or returns an already
     * cached version.
     * 
     * @since 2.0
     * @param call the incoming call
     * @param command the command being used to execute
     * @param chain the filter chain
     * @param policy the cache policy
     * @param maxAge the max age of the content's cache period
     * @param maxAgeUnit the unit of maxAge
     * @return the cached content
     * @throws IpcCommandExecutionException if chain execution failed
     */
    Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain,
        CachePolicy policy, long maxAge, TimeUnit maxAgeUnit) throws IpcCommandExecutionException;

    /**
     * Invalidates all cached versions of an {@link IpcCommand}.
     *
     * @since 2.1
     * @param command the IpcCommand definition to invalidate
     */
    void invalidate(Class<? extends IpcCommand> command);

    /**
     * Invalidates cached versions of an {@link IpcCommand} matching a predicate.
     *
     * @since 2.1
     * @param command the IpcCommand definition to invalidate
     * @param predicate a predicate to only invalidate self-filtered entries
     */
    void invalidate(Class<? extends IpcCommand> command, Predicate<CacheKey> predicate);
    
}

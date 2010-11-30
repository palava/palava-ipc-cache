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

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * A CachePolicy decides how to cache an {@link IpcCommand}.
 *
 * Since v2.1 {@link CacheKey} keys are required to reflect the
 * stored commands.
 * 
 * @author Willi Schoenborn
 * @author Oliver Lorenz
 * @author Tobias Sarnowski
 */
public enum CachePolicy implements CacheKeyFactory {

    /**
     * <p>
     * A CachePolicy where the fully qualified name of a command and its arguments 
     * are considered for caching.
     * </p>
     * <p>
     * The order of the arguments is important.
     * This means that one call with the arguments {"id": "34534", "limit": 10}
     * and another one with the same command but the arguments {"limit": 10, "id": "34534"}
     * will be cached separately.
     * </p>
     * <p>
     * If no arguments are provided then they are not considered for caching
     * and only the name of the command class is considered for caching.
     * In this case CachePolicy.SMART behaves equivalent to {@link #STATIC}.
     * </p>
     */
    SMART {
        
        @Override
        public CacheKey create(IpcCall call, IpcCommand command) {
            Preconditions.checkNotNull(call, "Call");
            Preconditions.checkNotNull(command, "Command");
            return new DefaultCacheKey(command.getClass(), call.getArguments());
        }
        
    },
    
    /**
     * <p> A CachePolicy where only the fully qualified name of the command-class
     * is used for caching.
     * </p>
     * <p> This means that the Command annotated with this CachePolicy
     * always returns the same result after the first call.
     * It can only return a different result if a maxAge was set on
     * the {@code @Cache} annotation and that maxAge has passed since
     * the first call.
     * </p>
     * <p> <strong> Important: </strong>
     * If a command annotated with {@code @Cache} and the cache policy set to STATIC
     * is called with 1 or more arguments, then an IllegalStateException is thrown
     * by the cache filter. This implies that all commands that are annotated with
     * this CachePolicy <strong>must</strong> be called without any arguments.
     * </p>
     * <p> This CachePolicy is useful if a job does not require any arguments
     * and always returns the same result, for example a list of all
     * java enums (and their values) that the system knows about.
     * </p>
     */
    STATIC {

        @Override
        public CacheKey create(IpcCall call, IpcCommand command) {
            Preconditions.checkNotNull(call, "Call");
            Preconditions.checkNotNull(command, "Command");
            Preconditions.checkState(call.getArguments().isEmpty(),
                    "arguments not allowed for static-cached commands");
            return new DefaultCacheKey(command.getClass(), call.getArguments());
        }
        
    };
    
    @Override
    public abstract CacheKey create(IpcCall call, IpcCommand command);
    
}

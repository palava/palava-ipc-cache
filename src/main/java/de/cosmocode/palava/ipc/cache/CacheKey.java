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

import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCommand;

import java.io.Serializable;

/**
 * Represents a cache key.
 *
 * @author Tobias Sarnowski
 */
public interface CacheKey extends Serializable {

    /**
     * Provides the command class used by this cache key.
     *
     * @return the cached command type
     */
    Class<? extends IpcCommand> getCommand();

    /**
     * Provides the arguments used by this cache key.
     *
     * @return used arguments
     */
    IpcArguments getArguments();
}

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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * Default {@link CacheKey} implementation.
 * 
 * @author Tobias Sarnowski
 */
final class DefaultCacheKey implements CacheKey {

    private static final long serialVersionUID = -4241838764946165645L;
    
    private final Class<? extends IpcCommand> command;
    private final IpcArguments arguments;

    public DefaultCacheKey(Class<? extends IpcCommand> command, IpcArguments arguments) {
        this.command = Preconditions.checkNotNull(command, "Command");
        this.arguments = Preconditions.checkNotNull(arguments, "IpcArguments");
    }

    @Override
    public Class<? extends IpcCommand> getCommand() {
        return command;
    }

    @Override
    public IpcArguments getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof DefaultCacheKey) {
            final DefaultCacheKey other = DefaultCacheKey.class.cast(that);
            return Objects.equal(command, other.command) && Objects.equal(arguments, other.arguments);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(command, arguments);
    }

    @Override
    public String toString() {
        return "DefaultCacheKey{" + "command=" + command + ", arguments=" + arguments + "}";
    }
    
}

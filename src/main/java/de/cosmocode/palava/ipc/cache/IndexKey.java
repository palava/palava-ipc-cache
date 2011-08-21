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
import de.cosmocode.palava.ipc.IpcCommand;

import java.io.Serializable;
import java.util.Set;

/**
 * A "unique" key indentifying a {@link Set} of {@link CacheKey}s.
 * 
 * @author Tobias Sarnowski
 * @since 2.1
 */
final class IndexKey implements Serializable {

    private static final long serialVersionUID = 4387242225231963138L;

    // magic key to differentiate our key from static keys
    private static final String KEY = "INDEX_KEY";

    private final Class<? extends IpcCommand> command;

    private IndexKey(Class<? extends IpcCommand> command) {
        this.command = Preconditions.checkNotNull(command, "Command");
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof IndexKey) {
            final IndexKey other = IndexKey.class.cast(that);
            return Objects.equal(command, other.command);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(command, KEY);
    }

    /**
     * Static factory method for {@link IndexKey}s.
     * 
     * @since 2.1
     * @param command the command type
     * @return a new key
     */
    public static IndexKey create(Class<? extends IpcCommand> command) {
        return new IndexKey(command);
    }
    
    @Override
    public String toString() {
        return "IndexKey[" + command.getName() + "]";
    }
    
}

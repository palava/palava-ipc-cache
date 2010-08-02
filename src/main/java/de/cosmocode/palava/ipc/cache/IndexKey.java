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

import de.cosmocode.palava.ipc.IpcCommand;

import java.io.Serializable;

/**
 * @author Tobias Sarnowski
 */
final class IndexKey implements Serializable {

    // magic key to differentiate our key from static keys
    private String KEY = "INDEX_KEY";

    private Class<? extends IpcCommand> command;


    private IndexKey(Class<? extends IpcCommand> command) {
        this.command = command;
    }

    public static IndexKey create(Class<? extends IpcCommand> command) {
        return new IndexKey(command);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final IndexKey indexKey = (IndexKey) o;

        if (KEY != null ? !KEY.equals(indexKey.KEY) : indexKey.KEY != null) return false;
        if (command != null ? !command.equals(indexKey.command) : indexKey.command != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = KEY != null ? KEY.hashCode() : 0;
        result = 31 * result + (command != null ? command.hashCode() : 0);
        return result;
    }
}
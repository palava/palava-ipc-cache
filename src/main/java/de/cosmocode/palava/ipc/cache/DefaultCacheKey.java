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
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * @author Tobias Sarnowski
 */
public final class DefaultCacheKey implements CacheKey {

    private Class<? extends IpcCommand> ipcCommand;
    private IpcArguments ipcArguments;

    public DefaultCacheKey(Class<? extends IpcCommand> ipcCommand, IpcArguments ipcArguments) {
        this.ipcCommand = Preconditions.checkNotNull(ipcCommand, "Command");
        this.ipcArguments = Preconditions.checkNotNull(ipcArguments, "IpcArguments");
    }

    @Override
    public Class<? extends IpcCommand> getIpcCommand() {
        return ipcCommand;
    }

    @Override
    public IpcArguments getIpcArguments() {
        return ipcArguments;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DefaultCacheKey cacheKey = (DefaultCacheKey) o;

        if (!ipcCommand.equals(cacheKey.ipcCommand)) return false;
        if (!ipcArguments.equals(cacheKey.ipcArguments)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ipcCommand.hashCode();
        result = 31 * result + ipcArguments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DefaultCacheKey{" +
                "ipcCommand=" + ipcCommand +
                ", ipcArguments=" + ipcArguments +
                '}';
    }
}
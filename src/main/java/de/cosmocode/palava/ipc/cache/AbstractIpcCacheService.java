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

import com.google.common.base.Predicates;
import com.google.inject.Inject;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * Created by IntelliJ IDEA.
 * User: olor
 * Date: 04.01.11
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractIpcCacheService implements IpcCacheService {

    private CacheKeyFactory cacheKeyFactory = DefaultCacheKeyFactory.getFactory();

    public CacheKeyFactory getCacheKeyFactory() {
        return cacheKeyFactory;
    }

    @Inject(optional = true)
    public void setCacheKeyFactory(CacheKeyFactory cacheKeyFactory) {
        this.cacheKeyFactory = cacheKeyFactory;
    }

    public CacheKey createKey(IpcCall call, IpcCommand command) {
        return getCacheKeyFactory().create(call, command);
    }

    @Override
    public void invalidate(Class<? extends IpcCommand> command) {
        invalidate(command, Predicates.alwaysTrue());
    }

}

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
import com.google.inject.Inject;
import de.cosmocode.palava.cache.CacheService;
import de.cosmocode.palava.ipc.Ipc;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: olor
 * Date: 04.01.11
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
class GenericIpcCacheService extends AbstractIpcCacheService {
    private final static Logger LOG = LoggerFactory.getLogger(GenericIpcCacheService.class);

    private final CacheService cacheService;

    @Inject
    GenericIpcCacheService(@Ipc CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public Map<String, Object> getCachedResult(IpcCommand command, IpcCall call) {
        return cacheService.read(createKey(call, command));
    }

    @Override
    public void setCachedResult(IpcCommand command, IpcCall call, CacheDecision decision, Map<String, Object> result) {
        if (decision.getLifeTime() == 0) {
            cacheService.store(createKey(call, command), result);
        } else {
            cacheService.store(createKey(call, command), result, decision.getLifeTime(), decision.getLifeTimeUnit());
        }
    }

    @Override
    public void invalidate(Class<? extends IpcCommand> command, Predicate<? super CacheKey> predicate) {
        throw new UnsupportedOperationException();
    }
}

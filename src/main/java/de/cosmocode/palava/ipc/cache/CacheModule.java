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

import java.util.concurrent.TimeUnit;

import de.cosmocode.palava.ipc.FilterModule;
import de.cosmocode.palava.ipc.IpcCallFilter;

/**
 * 
 *
 * @since 
 * @author Willi Schoenborn
 */
public abstract class CacheModule extends FilterModule {

    protected IpcCallFilter custom(CachePolicy policy) {
        final CustomCacheFilter filter = new CustomCacheFilter(getProvider(CommandCacheService.class));
        filter.setPolicy(policy);
        return filter;
    }
    
    protected IpcCallFilter custom(CachePolicy policy, long maxAge, TimeUnit maxAgeUnit) {
        final CustomCacheFilter filter = new CustomCacheFilter(getProvider(CommandCacheService.class));
        filter.setPolicy(policy);
        filter.setMaxAge(maxAge);
        filter.setMaxAgeUnit(maxAgeUnit);
        return filter;
    }
    
}

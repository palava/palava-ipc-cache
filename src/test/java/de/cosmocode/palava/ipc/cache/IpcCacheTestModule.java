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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.cosmocode.palava.cache.BackedComputingCacheServiceModule;
import de.cosmocode.palava.cache.ConcurrentMapCacheServiceModule;
import de.cosmocode.palava.core.DefaultRegistryModule;
import de.cosmocode.palava.core.inject.TypeConverterModule;
import de.cosmocode.palava.core.lifecycle.LifecycleModule;
import de.cosmocode.palava.cron.Cron;
import de.cosmocode.palava.cron.DefaultCronServiceModule;
import de.cosmocode.palava.ipc.Ipc;

/**
 * <p>
 * Test module that can be used to inject a cache service (among other things).
 * </p>
 * <p>
 * This class was originally named CommandCacheTestModule, and renamed to IpcCacheTestModule in 3.0.
 * </p>
 *
 * @since 2.0
 * @author Willi Schoenborn
 * @author Oliver Lorenz
 */
public class IpcCacheTestModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new TypeConverterModule());
        binder.install(new LifecycleModule());
        binder.install(new DefaultRegistryModule());
        binder.install(new GenericIpcCacheServiceModule());
        binder.install(ConcurrentMapCacheServiceModule.annotatedWith(Real.class));
        binder.install(BackedComputingCacheServiceModule.annotatedWithAndBackedBy(Ipc.class, Real.class));
        binder.install(new DefaultCronServiceModule());
        binder.bind(ScheduledExecutorService.class).annotatedWith(Cron.class).
            toInstance(Executors.newScheduledThreadPool(5));
    }

}

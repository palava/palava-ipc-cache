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

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Module;

import de.cosmocode.junit.LoggingRunner;
import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.cache.BackedComputingCacheServiceModule;
import de.cosmocode.palava.cache.ConcurrentMapCacheServiceModule;
import de.cosmocode.palava.core.DefaultRegistryModule;
import de.cosmocode.palava.core.inject.TypeConverterModule;
import de.cosmocode.palava.core.lifecycle.LifecycleModule;
import de.cosmocode.palava.cron.Cron;
import de.cosmocode.palava.cron.DefaultCronServiceModule;
import de.cosmocode.palava.ipc.Ipc;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.MapIpcArguments;
import de.cosmocode.palava.ipc.cache.analyzer.TimeCached;
import de.cosmocode.palava.ipc.cache.analyzer.TimeCachedModule;

/**
 * Tests the {@link CacheFilter} under quasi real-world conditions.
 * Created on: 02.02.11
 *
 * @author Oliver Lorenz
 */
@RunWith(LoggingRunner.class)
public class TimeCachedIntegrationTest implements UnitProvider<CacheFilter> {

    @Override
    public CacheFilter unit() {
        return Guice.createInjector(
            new TypeConverterModule(),
            new LifecycleModule(),
            new DefaultRegistryModule(),
            new GenericIpcCacheServiceModule(),
            ConcurrentMapCacheServiceModule.annotatedWith(Real.class),
            BackedComputingCacheServiceModule.annotatedWithAndBackedBy(Ipc.class, Real.class),
            new DefaultCronServiceModule(),
            new Module() {
                @Override
                public void configure(Binder binder) {
                    binder.bind(ScheduledExecutorService.class).annotatedWith(Cron.class).
                            toInstance(Executors.newScheduledThreadPool(5));
                }
            },
            new TimeCachedModule()
        ).getInstance(CacheFilter.class);
    }

    /**
     * Creates an {@link IpcCall} mock.
     *
     * @param arguments the arguments to be returned by {@link IpcCall#getArguments()}
     * @return the mocked call
     */
    public IpcCall createCallMock(IpcArguments arguments) {
        final IpcCall call = EasyMock.createMock("call", IpcCall.class);
        EasyMock.expect(call.getArguments()).andReturn(arguments).atLeastOnce();
        EasyMock.replay(call);
        return call;
    }

    /**
     * Test execution and caching of a @{@link TimeCached} annotated command.
     *
     * @throws IpcCommandExecutionException should not happen
     */
    @Test
    public void timeCached() throws IpcCommandExecutionException {
        final IpcArguments arguments = new MapIpcArguments();
        arguments.put("a", 2);
        arguments.put("b", 4);
        final IpcCall call = createCallMock(arguments);
        final IpcCommand command = new Calculate();

        final CacheFilter unit = unit();
        final Map<String, Object> result = unit.filter(call, command, SimpleExecutingFilterChain.INSTANCE);

        Assert.assertEquals(6, result.get("sum"));
        Assert.assertEquals(8, result.get("product"));
        Assert.assertEquals(16, result.get("exponentiation"));

        final Map<String, Object> cached = unit.filter(call, command, SimpleExecutingFilterChain.INSTANCE);
        Assert.assertEquals(result, cached);
    }

}

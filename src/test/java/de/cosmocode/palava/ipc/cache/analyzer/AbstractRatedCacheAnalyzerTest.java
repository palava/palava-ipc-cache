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

package de.cosmocode.palava.ipc.cache.analyzer;

import com.google.common.collect.Maps;
import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.MapIpcArguments;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

/**
 * <p>
 * Abstract template to test the {@link RatedCacheAnalyzer}.
 * Provides no test methods itself, just sets up the environment for the analyzer.
 * </p>
 * <p>
 * Created on: 05.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
public abstract class AbstractRatedCacheAnalyzerTest implements UnitProvider<RatedCacheAnalyzer> {

    private final Framework framework = Palava.newFramework();

    protected IpcArguments arguments = new MapIpcArguments(Maps.<String, Object>newHashMap());
    protected IpcCall call;

    /**
     * Starts palava.
     */
    @Before
    public final void startPalava() {
        framework.start();
    }

    /**
     * Create a mock for call.
     */
    @Before
    public void mockCall() {
        call = EasyMock.createMock("call", IpcCall.class);
        EasyMock.expect(call.getArguments()).andReturn(arguments).atLeastOnce();
        EasyMock.replay(call);
    }

    /**
     * Verify mocked up call.
     */
    @After
    public void verifyCall() {
        EasyMock.verify(call);
    }

    /**
     * Stops palava.
     */
    @After
    public final void stopPalava() {
        framework.stop();
    }

    @Override
    public RatedCacheAnalyzer unit() {
        return framework.getInstance(RatedCacheAnalyzer.class);
    }
}

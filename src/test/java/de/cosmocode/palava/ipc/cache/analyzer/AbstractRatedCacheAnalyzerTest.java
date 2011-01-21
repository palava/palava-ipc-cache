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

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.core.lifecycle.Startable;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;

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
public abstract class AbstractRatedCacheAnalyzerTest implements UnitProvider<RatedCacheAnalyzer>, Startable {

    private final Framework framework = Palava.newFramework();

    @Before
    @Override
    public final void start() {
        framework.start();
    }

    @Override
    public final RatedCacheAnalyzer unit() {
        return framework.getInstance(RatedCacheAnalyzer.class);
    }

    /**
     * Creates an {@link IpcCall} mock.
     *
     * @param arguments the arguments to be returned by {@link IpcCall#getArguments()}
     * @return the mocked call
     */
    @Before
    public IpcCall createCallMock(IpcArguments arguments) {
        final IpcCall call = EasyMock.createMock("call", IpcCall.class);
        EasyMock.expect(call.getArguments()).andReturn(arguments).atLeastOnce();
        EasyMock.replay(call);
        return call;
    }

    @After
    @Override
    public final void stop() {
        framework.stop();
    }
    
}

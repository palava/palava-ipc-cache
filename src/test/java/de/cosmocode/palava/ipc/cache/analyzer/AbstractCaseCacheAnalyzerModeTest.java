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

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.core.lifecycle.Startable;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.IpcCacheTestModule;

/**
 * <p>
 * Abstract test class for testing filtering with different modes on {@link CaseCacheAnalyzer}.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public abstract class AbstractCaseCacheAnalyzerModeTest implements UnitProvider<CaseCacheAnalyzer>, Startable {

    @SuppressWarnings("unchecked")
    private static final Class<? extends CachePredicate>[] ALL_FILTERS = new Class[] {
        HasDateFilter.class, 
        AccountIdIsFiveFilter.class
    };

    private final Framework framework = Palava.newFramework(new IpcCacheTestModule(), new Properties());

    /**
     * Returns the mode that is to be tested.
     * @return the mode to be tested, returned by {@link CaseCached#mode()}
     */
    protected abstract CaseCacheMode mode();

    @Before
    @Override
    public final void start() {
        framework.start();
    }

    /**
     * Creates a mocked {@link CaseCached} annotation.
     *
     * @return the mocked annotation
     */
    public final CaseCached annotation() {
        final CaseCached annotation = EasyMock.createMock(CaseCached.class);
        
        EasyMock.expect(annotation.mode()).andReturn(mode()).atLeastOnce();
        EasyMock.expect(annotation.predicates()).andReturn(ALL_FILTERS).atLeastOnce();
        
        EasyMock.expect(annotation.lifeTime()).andStubReturn(0L);
        EasyMock.expect(annotation.lifeTimeUnit()).andStubReturn(TimeUnit.MINUTES);
        EasyMock.expect(annotation.idleTime()).andStubReturn(0L);
        EasyMock.expect(annotation.idleTimeUnit()).andStubReturn(TimeUnit.MINUTES);
        EasyMock.replay(annotation);
        
        return annotation;
    }

    /**
     * Creates a mocked {@link IpcCommand}.
     * 
     * @return the mocked command
     */
    public final IpcCommand command() {
        final IpcCommand command = EasyMock.createMock(IpcCommand.class);
        EasyMock.replay(command);
        return command;
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
     * Tests with a date argument.
     */
    @Test
    public abstract void dateMatches();

    /**
     * Tests with an account id of 5.
     */
    @Test
    public abstract void accountIdMatches();

    /**
     * Tests with both a date argument and an account id of 5.
     */
    @Test
    public abstract void bothMatch();

    /**
     * Tests with none matching arguments.
     */
    @Test
    public abstract void noneMatches();

    @After
    @Override
    public final void stop() {
        framework.stop();
    }

    @Override
    public final CaseCacheAnalyzer unit() {
        return framework.getInstance(CaseCacheAnalyzer.class);
    }

}

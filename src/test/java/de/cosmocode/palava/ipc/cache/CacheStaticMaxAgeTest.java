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

import org.junit.Before;
import org.junit.Test;

import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * Tests {@link CacheFilter} on some IpcCommands with the {@code @Cache} annotation 
 * and the cachePolicy set to {@link CachePolicy#SMART}.
 * It also has a maxAge ({@link Cached#maxAge()}) set, and it tests that the cached command
 * expires from the cache after the given amount of time.
 * 
 * @author Oliver Lorenz
 */
public final class CacheStaticMaxAgeTest extends AbstractCacheTest {
    
    /**
     * <p> This is the normal time to wait (in milliseconds) for normal calls.
     * </p>
     * <p> The following must be true: WAIT_TIME < MAX_AGE * 1000
     * </p>
     * */
    private static final long WAIT_TIME = 1000;
    
    /** maximum age to cache command, in seconds. */
    private static final long MAX_AGE = 2;
    
    private CacheFilter filter;
    private CommandCacheService service;
    private final IpcCommand command;
    private final IpcCommand namedCommand1;
    private final IpcCommand namedCommand2;
    
    
    public CacheStaticMaxAgeTest() {
        this.command = new StaticCacheCommand();
        this.namedCommand1 = new NamedCommand1();
        this.namedCommand2 = new NamedCommand2();
    }
    
    /**
     * Runs before each test.
     */
    @Before
    public void initialize() {
        this.filter = getFramework().getInstance(CacheFilter.class);
        this.service = getFramework().getInstance(CommandCacheService.class);
        this.service.setFactory(SCOPE_KEY_FACTORY);
    }
    
    @Override
    protected IpcCallFilter getFilter() {
        return filter;
    }
    
    @Override
    protected IpcCommand getCommand() {
        return command;
    }
    
    @Override
    protected IpcCommand getNamedCommand1() {
        return namedCommand1;
    }
    
    @Override
    protected IpcCommand getNamedCommand2() {
        return namedCommand2;
    }
    
    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.SMART)}. */
    @Cached(policy = CachePolicy.STATIC, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    private class StaticCacheCommand extends DummyCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.SMART)}. */
    @Cached(policy = CachePolicy.STATIC, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    private class NamedCommand1 extends DummyCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.SMART)}. */
    @Cached(policy = CachePolicy.STATIC, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    private class NamedCommand2 extends DummyCommand { }
    
    /*
     * Same command name, no scope
     */
    
    /**
     * Tests the {@link CacheFilter} with no arguments.
     */
    @Test
    public void noArguments() {
        setupNoArguments();
        assertCached(WAIT_TIME);
    }
    
    /**
     * Tests the {@link CacheFilter} with no arguments and sleeps until the max age has passed.
     */
    @Test
    public void noArgumentsTimeout() {
        setupNoArguments();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }
    
    /*
     * Call Scope
     */
    
    /**
     * Tests the {@link CacheFilter} with the same call scope parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsSameCallScope() {
        setupNoArguments();
        setupSameCallScope();
        assertCached(WAIT_TIME);
    }
    
    /**
     * Tests the {@link CacheFilter} with different scope context parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsDifferentCallScope() {
        setupNoArguments();
        setupDifferentCallScope();
        assertCached(WAIT_TIME);
    }

    /*
     * Connection Scope
     */
    
    /**
     * Tests the {@link CacheFilter} with the same connection scope parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsSameConnectionScope() {
        setupSameConnectionScope();
        setupNoArguments();
        assertCached(WAIT_TIME);
    }
    
    /**
     * Tests the {@link CacheFilter} with different connection scope context parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsDifferentConnectionScope() {
        setupDifferentConnectionScope();
        setupNoArguments();
        assertCached(WAIT_TIME);
    }

    /*
     * Session Scope
     */
    
    /**
     * Tests the {@link CacheFilter}
     * with the same session scope parameters and no arguments.
     */
    @Test
    public void noArgumentsSameSessionScope() {
        setupSameSessionScope();
        setupNoArguments();
        assertCached(WAIT_TIME);
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with different session scope context parameters and no arguments.
     */
    @Test
    public void noArgumentsDifferentSessionScope() {
        setupDifferentSessionScope();
        setupNoArguments();
        assertCached(WAIT_TIME);
    }

}

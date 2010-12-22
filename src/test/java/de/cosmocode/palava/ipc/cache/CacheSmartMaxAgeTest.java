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
public final class CacheSmartMaxAgeTest extends AbstractCacheTest {
    
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
    
    public CacheSmartMaxAgeTest() {
        this.command = new SmartCacheCommand();
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
    @Cached(policy = CachePolicy.SMART, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    private class SmartCacheCommand extends DummyCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.SMART)}. */
    @Cached(policy = CachePolicy.SMART, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    private class NamedCommand1 extends DummyCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.SMART)}. */
    @Cached(policy = CachePolicy.SMART, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    private class NamedCommand2 extends DummyCommand { }
    
    
    private void initScopes() {
        this.service.setFactory(SCOPE_KEY_FACTORY);
    }
    
    /*
     * Same command name, no scope
     */
    
    /**
     * Tests the {@link CacheFilter} with same arguments.
     */
    @Test
    public void sameArguments() {
        setupSameArguments();
        assertCached(WAIT_TIME);
    }
    
    /**
     * Tests the {@link CacheFilter} with no arguments.
     */
    @Test
    public void noArguments() {
        setupNoArguments();
        assertCached(WAIT_TIME);
    }
    
    /**
     * Tests the {@link CacheFilter} with different arguments.
     */
    @Test
    public void differentArguments() {
        setupDifferentArguments();
        assertNotCached();
    }
    
    
    /*
     * Call Scope
     */
    
    /**
     * Tests the {@link CacheFilter} with the same arguments
     * and the same call scope parameters.
     */
    @Test
    public void sameArgumentsSameCallScope() {
        initScopes();
        setupSameArguments();
        setupSameCallScope();
        assertCached(WAIT_TIME);
    }

    /**
     * Tests the {@link CacheFilter} with the same arguments
     * but different call scope parameters.
     */
    @Test
    public void sameArgumentsDifferentCallScope() {
        initScopes();
        setupSameArguments();
        setupDifferentCallScope();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with the same call scope parameters
     * but different arguments.
     */
    @Test
    public void differentArgumentsSameCallScope() {
        initScopes();
        setupDifferentArguments();
        setupSameCallScope();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with different call scope parameters
     * and different arguments.
     */
    @Test
    public void differentArgumentsDifferentCallScope() {
        initScopes();
        setupDifferentArguments();
        setupDifferentCallScope();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with the same call scope parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsSameCallScope() {
        initScopes();
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
        initScopes();
        setupNoArguments();
        setupDifferentCallScope();
        assertNotCached();
    }
    

    /*
     * Connection Scope
     */
    
    /**
     * Tests the {@link CacheFilter} with the same arguments
     * and the same connection scope parameters.
     */
    @Test
    public void sameArgumentsSameConnectionScope() {
        initScopes();
        setupSameConnectionScope();
        setupSameArguments();
        assertCached(WAIT_TIME);
    }

    /**
     * Tests the {@link CacheFilter} with the same arguments
     * but different connection scope parameters.
     */
    @Test
    public void sameArgumentsDifferentConnectionScope() {
        initScopes();
        setupDifferentConnectionScope();
        setupSameArguments();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with the same connection scope parameters
     * but different arguments.
     */
    @Test
    public void differentArgumentsSameConnectionScope() {
        initScopes();
        setupSameConnectionScope();
        setupDifferentArguments();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with different connection scope parameters
     * and different arguments.
     */
    @Test
    public void differentArgumentsDifferentConnectionScope() {
        initScopes();
        setupDifferentConnectionScope();
        setupDifferentArguments();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with the same connection scope parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsSameConnectionScope() {
        initScopes();
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
        initScopes();
        setupDifferentConnectionScope();
        setupNoArguments();
        assertNotCached();
    }
    

    /*
     * Session Scope
     */
    
    /**
     * Tests the {@link CacheFilter} with the same arguments
     * and the same session scope parameters.
     */
    @Test
    public void sameArgumentsSameSessionScope() {
        initScopes();
        setupSameSessionScope();
        setupSameArguments();
        assertCached(WAIT_TIME);
    }

    /**
     * Tests the {@link CacheFilter}
     * with the same arguments but different session scope parameters.
     */
    @Test
    public void sameArgumentsDifferentSessionScope() {
        initScopes();
        setupDifferentSessionScope();
        setupSameArguments();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with the same session scope parameters but different arguments.
     */
    @Test
    public void differentArgumentsSameSessionScope() {
        initScopes();
        setupSameSessionScope();
        setupDifferentArguments();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with different session scope parameters and different arguments.
     */
    @Test
    public void differentArgumentsDifferentSessionScope() {
        initScopes();
        setupDifferentSessionScope();
        setupDifferentArguments();
        assertNotCached();
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with the same session scope parameters and no arguments.
     */
    @Test
    public void noArgumentsSameSessionScope() {
        initScopes();
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
        initScopes();
        setupDifferentSessionScope();
        setupNoArguments();
        assertNotCached();
    }
    

    /*
     * Timeout
     */
    
    /**
     * Tests the {@link CacheFilter} with same arguments and sleeps until the max age has passed.
     */
    @Test
    public void sameArgumentsTimeout() {
        setupSameArguments();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }
    
    /**
     * Tests the {@link CacheFilter} with no arguments and sleeps until the max age has passed.
     */
    @Test
    public void noArgumentsTimeout() {
        setupNoArguments();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }

}

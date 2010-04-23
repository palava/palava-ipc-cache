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

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * Tests {@link CacheFilter} on some IpcCommands with the {@code @Cache} annotation 
 * and the cachePolicy set to {@link CachePolicy#STATIC}.
 * 
 * @author Oliver Lorenz
 */
public final class CacheStaticTest extends AbstractCacheTest {
    
    private final IpcCallFilter filter;
    private final IpcCommand command;
    
    private final IpcCommand namedCommand1;
    private final IpcCommand namedCommand2;
    
    
    public CacheStaticTest() {
        final CacheFilter cacheFilter = new CacheFilter(new SimpleCacheService());
        cacheFilter.setCallScopeKeys(StringUtils.join(CALL_KEYS, ','));
        cacheFilter.setConnectionScopeKeys(StringUtils.join(CONNECTION_KEYS, ','));
        cacheFilter.setSessionScopeKeys(StringUtils.join(SESSION_KEYS, ','));
        this.filter = cacheFilter;
        this.command = new StaticCacheCommand();
        this.namedCommand1 = new NamedCommand1();
        this.namedCommand2 = new NamedCommand2();
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
    
    
    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.STATIC)}. */
    @Cache(policy = CachePolicy.STATIC)
    private class StaticCacheCommand extends DummyCommand implements IpcCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.STATIC)}. */
    @Cache(policy = CachePolicy.STATIC)
    private class NamedCommand1 extends DummyCommand implements IpcCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.STATIC)}. */
    @Cache(policy = CachePolicy.STATIC)
    private class NamedCommand2 extends DummyCommand implements IpcCommand { }
    
    
    /*
     * Same command name, no scope
     */
    
    /**
     * Tests the {@link CacheFilter} with same arguments.
     */
    @Test
    public void sameArguments() {
        setupSameArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with different arguments.
     */
    @Test
    public void differentArguments() {
        setupDifferentArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with no arguments.
     */
    @Test
    public void noArguments() {
        setupNoArguments();
        assertCached();
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
        setupSameArguments();
        setupSameCallScope();
        assertCached();
    }

    /**
     * Tests the {@link CacheFilter} with the same arguments
     * but different call scope parameters.
     */
    @Test
    public void sameArgumentsDifferentCallScope() {
        setupSameArguments();
        setupDifferentCallScope();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with the same call scope parameters
     * but different arguments.
     */
    @Test
    public void differentArgumentsSameCallScope() {
        setupDifferentArguments();
        setupSameCallScope();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with different call scope parameters
     * and different arguments.
     */
    @Test
    public void differentArgumentsDifferentCallScope() {
        setupDifferentArguments();
        setupDifferentCallScope();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with the same call scope parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsSameCallScope() {
        setupNoArguments();
        setupSameCallScope();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with different scope context parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsDifferentCallScope() {
        setupNoArguments();
        setupDifferentCallScope();
        assertCached();
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
        setupSameConnectionScope();
        setupSameArguments();
        assertCached();
    }

    /**
     * Tests the {@link CacheFilter} with the same arguments
     * but different connection scope parameters.
     */
    @Test
    public void sameArgumentsDifferentConnectionScope() {
        setupDifferentConnectionScope();
        setupSameArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with the same connection scope parameters
     * but different arguments.
     */
    @Test
    public void differentArgumentsSameConnectionScope() {
        setupSameConnectionScope();
        setupDifferentArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with different connection scope parameters
     * and different arguments.
     */
    @Test
    public void differentArgumentsDifferentConnectionScope() {
        setupDifferentConnectionScope();
        setupDifferentArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with the same connection scope parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsSameConnectionScope() {
        setupSameConnectionScope();
        setupNoArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter} with different connection scope context parameters
     * and no arguments.
     */
    @Test
    public void noArgumentsDifferentConnectionScope() {
        setupDifferentConnectionScope();
        setupNoArguments();
        assertCached();
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
        setupSameSessionScope();
        setupSameArguments();
        assertCached();
    }

    /**
     * Tests the {@link CacheFilter}
     * with the same arguments but different session scope parameters.
     */
    @Test
    public void sameArgumentsDifferentSessionScope() {
        setupDifferentSessionScope();
        setupSameArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with the same session scope parameters but different arguments.
     */
    @Test
    public void differentArgumentsSameSessionScope() {
        setupSameSessionScope();
        setupDifferentArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with different session scope parameters and different arguments.
     */
    @Test
    public void differentArgumentsDifferentSessionScope() {
        setupDifferentSessionScope();
        setupDifferentArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with the same session scope parameters and no arguments.
     */
    @Test
    public void noArgumentsSameSessionScope() {
        setupSameSessionScope();
        setupNoArguments();
        assertCached();
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with different session scope context parameters and no arguments.
     */
    @Test
    public void noArgumentsDifferentSessionScope() {
        setupDifferentSessionScope();
        setupNoArguments();
        assertCached();
    }

}

/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.ipc.cache;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * Tests {@link CacheFilter} on some IpcCommands with the {@code @Cache} annotation 
 * and the cachePolicy set to {@link CachePolicy#SMART}.
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
    
    private final IpcCallFilter filter;
    
    private final IpcCommand command;
    private final IpcCommand namedCommand1;
    private final IpcCommand namedCommand2;
    
    
    public CacheSmartMaxAgeTest() {
        final CacheFilter cacheFilter = new CacheFilter(new SimpleCacheService());
        cacheFilter.setCallScopeKeys(StringUtils.join(CALL_KEYS, ','));
        cacheFilter.setConnectionScopeKeys(StringUtils.join(CONNECTION_KEYS, ','));
        cacheFilter.setSessionScopeKeys(StringUtils.join(SESSION_KEYS, ','));
        this.filter = cacheFilter;
        this.command = new SmartCacheCommand();
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
    
    
    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.SMART)}. */
    @Cache(policy = CachePolicy.SMART, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    protected class SmartCacheCommand extends DummyCommand implements IpcCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.SMART)}. */
    @Cache(policy = CachePolicy.SMART, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    protected class NamedCommand1 extends DummyCommand implements IpcCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.SMART)}. */
    @Cache(policy = CachePolicy.SMART, maxAge = MAX_AGE, maxAgeUnit = TimeUnit.SECONDS)
    protected class NamedCommand2 extends DummyCommand implements IpcCommand { }
    
    
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
    
    /**
     * Tests the {@link CacheFilter} with the same arguments
     * and the same call scope parameters and sleeps until the max age has passed.
     */
    @Test
    public void sameArgumentsSameCallScopeTimeout() {
        setupSameArguments();
        setupSameCallScope();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }
    
    /**
     * Tests the {@link CacheFilter} with the same call scope parameters
     * and no arguments and sleeps until the max age has passed.
     */
    @Test
    public void noArgumentsSameCallScopeTimeout() {
        setupNoArguments();
        setupSameCallScope();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }
    
    /**
     * Tests the {@link CacheFilter} with the same arguments
     * and the same connection scope parameters and sleeps until the max age has passed.
     */
    @Test
    public void sameArgumentsSameConnectionScopeTimeout() {
        setupSameConnectionScope();
        setupSameArguments();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }
    
    /**
     * Tests the {@link CacheFilter} with the same connection scope parameters
     * and no arguments and sleeps until the max age has passed.
     */
    @Test
    public void noArgumentsSameConnectionScopeTimeout() {
        setupSameConnectionScope();
        setupNoArguments();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }
    
    /**
     * Tests the {@link CacheFilter} with the same arguments
     * and the same session scope parameters and sleeps until the max age has passed.
     */
    @Test
    public void sameArgumentsSameSessionScopeTimeout() {
        setupSameSessionScope();
        setupSameArguments();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }
    
    /**
     * Tests the {@link CacheFilter}
     * with the same session scope parameters and no arguments and sleeps until the max age has passed.
     */
    @Test
    public void noArgumentsSameSessionScopeTimeout() {
        setupSameSessionScope();
        setupNoArguments();
        assertNotCached(TimeUnit.MILLISECONDS.convert(MAX_AGE + 1, TimeUnit.SECONDS));
    }

}

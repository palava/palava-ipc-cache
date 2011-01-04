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

import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCommand;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

/**
 * Tests the filters of {@link Cached}: {@link Cached#filters()}
 * with the filter mode {@link FilterMode#ANY}.
 */
public final class CacheFilteredAnyTest extends AbstractCacheTest {

    private CacheFilter filter;
    private final IpcCommand command;

    public CacheFilteredAnyTest() {
        this.command = new FilteredCacheCommand();
    }

    /**
     * Runs before each test.
     */
    @Before
    public void initialize() {
        this.filter = getFramework().getInstance(CacheFilter.class);
        getFramework().getInstance(CommandCacheService.class).setFactory(SCOPE_KEY_FACTORY);
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
        throw new UnsupportedOperationException("Unused");
    }

    @Override
    protected IpcCommand getNamedCommand2() {
        throw new UnsupportedOperationException("Unused");
    }

    /** A dummy command with a defined filter. */
    @Cached(
        filters = {
            EnglishLocaleCacheFilter.class,
            IpcArgumentsCacheFilter.class
        },
        filterMode = FilterMode.ANY
    )
    private class FilteredCacheCommand extends DummyCommand { }

    /**
     * Verifies that command gets filtered out if no scope variables or arguments are set.
     */
    @Test
    public void filteredOutNoScope() {
        setupNoArguments();
        assertNotCached();
    }

    /**
     * Verifies that the command gets filtered out with the wrong language set in call context.
     */
    @Test
    public void filteredOutWrongLocale() {
        setupNoArguments();
        setupCall(getCall1(), "call", Locale.GERMAN);
        setupCall(getCall2(), "call", Locale.GERMAN);
        assertNotCached();
    }

    /**
     * Verifies that the command is normally cached if the right language is set in call context.
     */
    @Test
    public void passedLocaleFilter() {
        setupNoArguments();
        setupCall(getCall1(), "call", Locale.ENGLISH);
        setupCall(getCall2(), "call", Locale.ENGLISH);
        assertCached();
    }

    /**
     * Verifies that the command is normally cached if the right arguments are set.
     */
    @Test
    public void passedArgumentsFilter() {
        setupSameArguments();
        assertCached();
    }

    /**
     * Verifies that the command is normally cached
     * if the right language is set in call context and the right arguments are set.
     */
    @Test
    public void passedBothFilters() {
        setupSameArguments();
        setupCall(getCall1(), "call", Locale.ENGLISH);
        setupCall(getCall2(), "call", Locale.ENGLISH);
        assertCached();
    }

}

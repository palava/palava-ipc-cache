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
 * Tests the filters of {@link Cached}: {@link de.cosmocode.palava.ipc.cache.Cached#filters()}.
 */
public final class CacheFilteredTest extends AbstractCacheTest {

    private CacheFilter filter;
    private CommandCacheService service;
    private final IpcCommand command;

    public CacheFilteredTest() {
        this.command = new FilteredCacheCommand();
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
        return command;
    }

    @Override
    protected IpcCommand getNamedCommand2() {
        return command;
    }

    /** A dummy command with a defined filter. */
    @Cached(filters = {EnglishLocaleCacheFilter.class})
    private class FilteredCacheCommand extends DummyCommand { }

    private void initScopes() {
        this.service.setFactory(SCOPE_KEY_FACTORY);
    }

    @Test
    public void filteredOutNoScope() {
        setupSameArguments();
        assertNotCached();
    }

    @Test
    public void filteredOut() {
        initScopes();
        setupNoArguments();
        setupCall(getCall1(), "call", Locale.GERMAN);
        setupCall(getCall2(), "call", Locale.GERMAN);

        assertNotCached();
    }

    @Test
    public void passedFilter() {
        initScopes();
        setupNoArguments();
        setupCall(getCall1(), "call", Locale.ENGLISH);
        setupCall(getCall2(), "call", Locale.ENGLISH);

        assertCached();
    }

}

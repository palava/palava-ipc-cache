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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.IpcConnection;
import de.cosmocode.palava.ipc.IpcSession;

/**
 * <p> An abstract test that provides some helper methods to call IpcCommands
 * and a method that returns random content.
 * </p>
 * <p> This class tests only the cases with different IpcCommands, which should never be cached.
 * </p>
 * 
 * @author Oliver Lorenz
 */
public abstract class AbstractCacheTest {

    public static final String EXPECT_CACHED = "should have been cached, but was not.";
    public static final String EXPECT_NOT_CACHED = "was illegally cached.";
    

    /** "call", "lang". */
    public static final List<String> CALL_KEYS = ImmutableList.of("call", "lang");
    /** "connection", "lang". */
    public static final List<String> CONNECTION_KEYS = ImmutableList.of("connection", "lang");
    /** "session", "lang". */
    public static final List<String> SESSION_KEYS = ImmutableList.of("session", "lang");
    
    
    private IpcSession session1;
    private IpcSession session2;
    
    private IpcConnection connection1;
    private IpcConnection connection2;
    
    private IpcCall call1;
    private IpcCall call2;
    
    private IpcCommand command1;
    private IpcCommand command2;
    
    
    public AbstractCacheTest() {
        session1 = mockSession();
        session2 = mockSession();
        connection1 = mockConnection(session1);
        connection2 = mockConnection(session2);
        call1 = new SimpleCall(connection1);
        call2 = new SimpleCall(connection2);
        // commands are left for @Before
    }

    
    /**
     * Get the {@link IpcCallFilter} implementation to call on {@link #filterAndExecute(Call)}.
     * @return an IpcCallFilter that handles the caching
     */
    protected abstract IpcCallFilter getFilter();
    
    /**
     * Return an {@link IpcCommand}.
     * @return an IpcCommand
     */
    protected abstract IpcCommand getCommand();
    
    /**
     * Return a named {@link IpcCommand}.
     * @return a named IpcCommand
     */
    protected abstract IpcCommand getNamedCommand1();
    
    /**
     * Return a named {@link IpcCommand} that has a different name than {@link #getNamedCommand1()}.
     * @return a named IpcCommand
     */
    protected abstract IpcCommand getNamedCommand2();
    
    /**
     * Sets default values for command1 and command2.
     * Sets both of them to {@link #getCommand()}.
     */
    @Before
    public final void setCommands() {
        this.command1 = getCommand();
        this.command2 = getCommand();
    }
    
    
    protected final IpcCall getCall1() {
        return call1;
    }
    
    protected final IpcCall getCall2() {
        return call2;
    }
    
    protected final IpcCommand getCommand1() {
        return command1;
    }
    
    protected final IpcCommand getCommand2() {
        return command2;
    }
    
    protected final IpcConnection getConnection1() {
        return connection1;
    }
    
    protected final IpcConnection getConnection2() {
        return connection2;
    }
    
    protected final IpcSession getSession1() {
        return session1;
    }
    
    protected final IpcSession getSession2() {
        return session2;
    }
    
    
    /**
     * Executes the calls and asserts that second call returned the same result as the first,
     * which means that the first call was cached.
     */
    protected void assertCached() {
        this.assertCached(0);
    }
    
    /**
     * Executes the calls and asserts that second call returned the same result as the first,
     * which means that the first call was cached.
     * @param timeout the time (in milliseconds) to wait between the two executions
     */
    protected void assertCached(final long timeout) {
        try {
            final Map<String, Object> content1 = filterAndExecute(call1, command1);
            Thread.sleep(timeout);
            final Map<String, Object> content2 = filterAndExecute(call2, command2);

            EasyMock.verify(session1, session2, connection1, connection2);
            
            Assert.assertSame(EXPECT_CACHED, content1, content2);
        } catch (IpcCommandExecutionException e) {
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Timeout was illegally interrupted", e);
        }
    }
    
    /**
     * Executes the calls and asserts that the second call returned a different result as the first,
     * which means that the first call was cached.
     */
    protected void assertNotCached() {
        this.assertNotCached(0);
    }
    
    /**
     * Executes the calls and asserts that the second call returned a different result as the first,
     * which means that the first call was not cached.
     * @param timeout the time (in milliseconds) to wait between the two executions
     */
    protected void assertNotCached(final long timeout) {
        try {
            final Map<String, Object> content1 = filterAndExecute(call1, command1);
            Thread.sleep(timeout);
            final Map<String, Object> content2 = filterAndExecute(call2, command2);

            EasyMock.verify(session1, session2, connection1, connection2);

            Assert.assertNotSame(EXPECT_NOT_CACHED, content1, content2);
        } catch (IpcCommandExecutionException e) {
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Timeout was illegally interrupted", e);
        }
    }
    
    /**
     * Returns random content on every invocation.
     * @return a Map with the mapping "result" to a random UUID
     */
    protected Map<String, Object> randomContent() {
        final Map<String, Object> content = Maps.newLinkedHashMap();
        content.put("result", UUID.randomUUID());
        return content;
    }
    
    /**
     * Filters the given call and returns either the filtered content or a {@link #randomContent()}.
     * @param call the call to filter on
     * @param command the command to execute
     * @return a {@link #randomContent()} or the cached Content from {@link #getFilter()}
     * @throws IpcCommandExecutionException if the filter given by {@link #getFilter()} throws it
     */
    protected Map<String, Object> filterAndExecute(final IpcCall call, final IpcCommand command)
        throws IpcCommandExecutionException {
        
        final IpcCallFilterChain chain = EasyMock.createMock(IpcCallFilterChain.class);
        EasyMock.expect(chain.filter(call, command)).andStubReturn(randomContent());
        EasyMock.replay(chain);
        return getFilter().filter(call, command, chain);
    }
    
    /**
     * Mocks up an {@link IpcSession}. It has the given key value pairs set.
     * @param values a list of values, in the same order as in {@value #SESSION_KEYS}
     * @return a mocked up {@link IpcSession}
     */
    protected IpcSession mockSession(final Object... values) {
        final IpcSession session = EasyMock.createMock(IpcSession.class);
        
        for (int i = 0; i < SESSION_KEYS.size(); i++) {
            if (i < values.length) {
                EasyMock.expect(session.get(SESSION_KEYS.get(i))).andStubReturn(values[i]);
            } else {
                EasyMock.expect(session.get(SESSION_KEYS.get(i))).andStubReturn(null);
            }
        }
        EasyMock.replay(session);
        
        return session;
    }
    
    /**
     * Mocks up an {@link IpcConnection}. It has the given key value pairs set.
     * @param session the session of the connection
     * @param values a list of values, in the same order as in {@value #CONNECTION_KEYS}
     * @return a mocked up {@link IpcConnection}
     */
    protected IpcConnection mockConnection(final IpcSession session, final Object... values) {
        Preconditions.checkNotNull(session, "Session");
        
        final IpcConnection connection = EasyMock.createMock(IpcConnection.class);

        for (int i = 0; i < CONNECTION_KEYS.size(); i++) {
            if (i < values.length) {
                EasyMock.expect(connection.get(CONNECTION_KEYS.get(i))).andStubReturn(values[i]);
            } else {
                EasyMock.expect(connection.get(CONNECTION_KEYS.get(i))).andStubReturn(null);
            }
        }
        EasyMock.expect(connection.getSession()).andStubReturn(session);
        EasyMock.replay(connection);
        
        return connection;
    }
    
    /**
     * Sets up the given call with several scope values, as in {@link #CALL_KEYS}.
     * @param call the call to set up
     * @param values a list of values, in the same order as in {@link #CALL_KEYS}
     */
    protected void setupCall(final IpcCall call, final Object... values) {
        Preconditions.checkNotNull(call, "Call");
        
        for (int i = 0; i < CALL_KEYS.size(); i++) {
            if (i < values.length) {
                call.set(CALL_KEYS.get(i), values[i]);
            }
        }
    }
    
    /**
     * Sets the calls up with the same arguments.
     */
    protected void setupSameArguments() {
        call1 = new SimpleCall(connection1, "arg1", "value1", "arg2", "value2");
        call2 = new SimpleCall(connection2, "arg1", "value1", "arg2", "value2");
    }
    
    /**
     * Sets the calls up with different arguments.
     */
    protected void setupDifferentArguments() {
        call1 = new SimpleCall(connection1, "arg1", "value1", "arg2", "value2");
        call2 = new SimpleCall(connection2, "arg1", "value1");
    }
    
    /**
     * Sets the calls up with no arguments each.
     */
    protected void setupNoArguments() {
        call1 = new SimpleCall(connection1);
        call2 = new SimpleCall(connection2);
    }
    
    /**
     * Sets the calls up with the same call scope variables. <br />
     * Must be called after any arguments call.
     */
    protected void setupSameCallScope() {
        setupCall(call1, "my_test_call", Locale.GERMAN);
        setupCall(call2, "my_test_call", Locale.GERMAN);
    }
    
    /**
     * Sets the calls up with different call scope variables. <br />
     * Must be called after any arguments call.
     */
    protected void setupDifferentCallScope() {
        setupCall(call1, "my_test_call", Locale.GERMAN);
        setupCall(call2, "my_other_call", Locale.ENGLISH);
    }
    
    /**
     * Sets the calls up with the same connection scope variables. <br />
     * Must be called before any arguments call.
     */
    protected void setupSameConnectionScope() {
        this.connection1 = mockConnection(session1, "same", Locale.GERMAN);
        this.connection2 = mockConnection(session2, "same", Locale.GERMAN);
    }
    
    /**
     * Sets the calls up with different connection scope variables. <br />
     * Must be called before any arguments call.
     */
    protected void setupDifferentConnectionScope() {
        this.connection1 = mockConnection(session1, "one", Locale.GERMAN);
        this.connection2 = mockConnection(session2, "two", Locale.ENGLISH);
    }
    
    /**
     * Sets the calls up with same session scope variables. <br />
     * Must be called before any other call.
     */
    protected void setupSameSessionScope() {
        this.session1 = mockSession("same", Locale.FRENCH);
        this.session2 = mockSession("same", Locale.FRENCH);
        this.connection1 = mockConnection(session1);
        this.connection2 = mockConnection(session2);
    }
    
    /**
     * Sets the calls up with different session scope variables. <br />
     * Must be called before any other call.
     */
    protected void setupDifferentSessionScope() {
        this.session1 = mockSession("session one", Locale.FRENCH);
        this.session2 = mockSession("session two", Locale.JAPANESE);
        this.connection1 = mockConnection(session1);
        this.connection2 = mockConnection(session2);
    }
    
    /**
     * Explicitly sets up the same commands. <br />
     * This is the default and it is not necessary to call this method.
     */
    protected void setupSameCommands() {
        this.command1 = getCommand();
        this.command2 = getCommand();
    }
    
    /**
     * Sets up two different commands for execution. <br />
     * This method can be called at any time.
     */
    protected void setupDifferentCommands() {
        this.command1 = getNamedCommand1();
        this.command2 = getNamedCommand2();
    }
    
    
    /**
     * A dummy {@link IpcCommand} that returns an
     * {@link AbstractCacheTest#randomContent()} on every execution.
     * 
     * @author Oliver Lorenz
     */
    protected abstract class DummyCommand implements IpcCommand {
        
        @Override
        public void execute(IpcCall call, Map<String, Object> result)
            throws IpcCommandExecutionException {
            
            result.putAll(randomContent());
        }
        
    }

}

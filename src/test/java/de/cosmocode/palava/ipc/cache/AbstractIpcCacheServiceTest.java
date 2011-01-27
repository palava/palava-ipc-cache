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

import java.util.Map;

import javax.annotation.Nullable;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.cache.CacheExpiration;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.MapIpcArguments;

/**
 * Abstract test for the {@link IpcCacheService} interface.
 * This does NOT test the invalidate method in depth, as it should be done for productive uses.
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public abstract class AbstractIpcCacheServiceTest implements UnitProvider<IpcCacheService> {
    
    private final CacheDecision decision = new EternalCacheDecision();

    // mocks
    private IpcCommand command;
    private IpcCall call;
    private IpcCall secondCall;

    // results (normal maps)
    private Map<String, Object> result;
    private Map<String, Object> secondResult;

    /**
     * Configuration hook for sub classes to enable/disable invalidation tests.
     *
     * @since 3.0
     * @return true if the unit under test supports {@link IpcCacheService#invalidate(Class)}
     */
    protected boolean supportsInvalidate() {
        return true;
    }

    /**
     * Creates all mocks before each test.
     */
    @Before
    public void createMocks() {
        // command
        command = EasyMock.createMock("command", IpcCommand.class);

        // call
        call = EasyMock.createMock("call", IpcCall.class);
        final IpcArguments arguments = new MapIpcArguments();
        arguments.put("account_id", 5);
        EasyMock.expect(call.getArguments()).andReturn(arguments).atLeastOnce();

        // create special second call for invalidation tests
        secondCall = EasyMock.createMock("secondCall", IpcCall.class);
        final IpcArguments secondArguments = new MapIpcArguments();
        secondArguments.put("account_id", 7);
        EasyMock.expect(secondCall.getArguments()).andReturn(secondArguments).atLeastOnce();

        // set all to replay mode (ready to use)
        EasyMock.replay(command, call, secondCall);
    }

    /**
     * Creates all results before each test.
     */
    @Before
    public void createResults() {
        result = Maps.newHashMap();
        result.put("test", "value");
        result.put("account_id", 5);

        secondResult = Maps.newHashMap();
        secondResult.put("test", "another value");
        secondResult.put("a boo", "fancy value");
        secondResult.put("account_id", 7);
    }

    /**
     * Verifies the mocks created for the first call.
     */
    protected void verifyFirstCallMocks() {
        EasyMock.verify(command, call);
    }

    /**
     * Verifies all created mocks.
     */
    protected void verifyAllMocks() {
        EasyMock.verify(command, call, secondCall);
    }

    private IpcCommandExecution returning(final Map<String, Object> r) {
        return new IpcCommandExecution() {
            
            @Override
            public Map<String, Object> call() throws IpcCommandExecutionException {
                return r;
            }
            
        };
    }
    
    /**
     * Tests {@link IpcCacheService#computeAndStore(IpcCommand, IpcCall, CacheExpiration, IpcCommandExecution)}.
     * 
     * @throws IpcCommandExecutionException should not happen
     */
    @Test
    public void computeAndStore() throws IpcCommandExecutionException {
        final Map<String, Object> computed = unit().computeAndStore(command, call, decision, returning(result));
        Assert.assertEquals(result, computed);
        verifyFirstCallMocks();
    }
    
    /**
     * Tests {@link IpcCacheService#read(IpcCommand, IpcCall)} before any store operation.
     *
     * @throws IpcCommandExecutionException should not happen
     */
    @Test
    public void readBeforeStore() throws IpcCommandExecutionException {
        final Map<String, Object> unitResult = unit().read(command, call);
        unit().computeAndStore(command, call, decision, returning(result));

        Assert.assertNull(unitResult);
        verifyFirstCallMocks();
    }

    /**
     * Tests {@link IpcCacheService#read(IpcCommand, IpcCall)} after a successful store operation.
     *
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void readAfterStore() throws IpcCommandExecutionException {
        unit().computeAndStore(command, call, decision, returning(result));
        final Map<String, Object> unitResult = unit().read(command, call);

        Assert.assertEquals(result, unitResult);
        verifyFirstCallMocks();
    }

    /**
     * Tests {@link IpcCacheService#invalidate(Class)}.
     *
     * @throws IpcCommandExecutionException should not happen
     */
    @Test
    public void invalidateAll() throws IpcCommandExecutionException {
        if (supportsInvalidate()) {

            // set cached results
            unit().computeAndStore(command, call, decision, returning(result));
            unit().computeAndStore(command, secondCall, decision, returning(secondResult));
    
            // call invalidate (main function)
            unit().invalidate(command.getClass());
    
            // assert that both calls are invalidated
            Assert.assertNull(unit().read(command, call));
            Assert.assertNull(unit().read(command, secondCall));
    
            verifyAllMocks();
        } else {
            try {
                unit().invalidate(command.getClass());
                Assert.fail("Expected " + unit() + ".invalidate(" + command.getClass() + ") to throw");
            } catch (UnsupportedOperationException expected) {
                return;
            }
        }
    }

    /**
     * Tests {@link IpcCacheService#invalidate(Class, Predicate)}.
     *
     * @throws IpcCommandExecutionException should not happen
     */
    @Test
    public void invalidateSingle() throws IpcCommandExecutionException {
        if (supportsInvalidate()) {
            // set cached results
            unit().computeAndStore(command, call, decision, returning(result));
            unit().computeAndStore(command, secondCall, decision, returning(secondResult));
            
            // invalidate with predicate
            unit().invalidate(command.getClass(), new Predicate<CacheKey>() {
                @Override
                public boolean apply(@Nullable CacheKey input) {
                    return input != null && input.getArguments().getInt("account_id") == 5;
                }
            });
            
            // assert that only the first call was invalidated
            Assert.assertNull(unit().read(command, call));
            Assert.assertEquals(secondResult, unit().read(command, secondCall));
            
            verifyAllMocks();
        } else {
            try {
                final IpcCacheService unit = unit();
                unit.invalidate(command.getClass());
                Assert.fail("Expected " + unit + ".invalidate(" + command.getClass() + ") to throw");
            } catch (UnsupportedOperationException expected) {
                return;
            }
        }
    }

}

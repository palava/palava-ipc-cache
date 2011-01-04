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

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.MapIpcArguments;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Abstract test for the {@link IpcCacheService} interface.
 * This does NOT test the invalidate method in depth, as it should be done for productive uses.
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public abstract class AbstractIpcCacheServiceTest implements UnitProvider<IpcCacheService> {

    private IpcCommand command;
    private IpcCall call;
    private CacheDecision decision;
    private Map<String, Object> result;

    private IpcCall secondCall;
    private Map<String, Object> secondResult;

    protected abstract boolean canNotInvalidate();

    @Before
    public void createMocks() {
        // command
        command = EasyMock.createMock("command", IpcCommand.class);

        // call
        call = EasyMock.createMock("call", IpcCall.class);
        final Map<String, Object> argumentsRaw = Maps.newHashMap();
        argumentsRaw.put("account_id", 5);
        final IpcArguments arguments = new MapIpcArguments(argumentsRaw);
        EasyMock.expect(call.getArguments()).andReturn(arguments).atLeastOnce();

        // decision
        decision = EasyMock.createMock("decision", CacheDecision.class);
        EasyMock.expect(decision.shouldCache()).andStubReturn(true);
        EasyMock.expect(decision.getLifeTime()).andStubReturn(0L);
        EasyMock.expect(decision.getLifeTimeUnit()).andStubReturn(TimeUnit.MINUTES);
        EasyMock.expect(decision.getIdleTime()).andStubReturn(0L);
        EasyMock.expect(decision.getIdleTimeUnit()).andStubReturn(TimeUnit.MINUTES);

        // create special second call which will not be verified
        secondCall = EasyMock.createMock("secondCall", IpcCall.class);
        final Map<String, Object> secondArgumentsRaw = Maps.newHashMap();
        secondArgumentsRaw.put("account_id", 7);
        final IpcArguments secondArguments = new MapIpcArguments(secondArgumentsRaw);
        EasyMock.expect(secondCall.getArguments()).andReturn(secondArguments).atLeastOnce();

        // set all to replay mode (ready to use)
        EasyMock.replay(command, call, decision, secondCall);
    }

    /**
     * Verifies the mocks created for the first call.
     */
    protected void verifyFirstCallMocks() {
        EasyMock.verify(command, call, decision);
    }

    /**
     * Verifies all created mocks.
     */
    protected void verifyAllMocks() {
        EasyMock.verify(command, call, decision, secondCall);
    }

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

    @Test
    public void getBeforeSet() {
        final Map<String, Object> unitResult = unit().getCachedResult(command, call);
        unit().setCachedResult(command, call, decision, result);

        Assert.assertNull(unitResult);
        verifyFirstCallMocks();
    }

    @Test
    public void getAfterSet() {
        unit().setCachedResult(command, call, decision, result);
        final Map<String, Object> unitResult = unit().getCachedResult(command, call);

        Assert.assertEquals(result, unitResult);
        verifyFirstCallMocks();
    }

    @Test
    public void invalidateAll() {
        if (canNotInvalidate()) {
            return;
        }

        // set cached results
        unit().setCachedResult(command, call, decision, result);
        unit().setCachedResult(command, secondCall, decision, secondResult);

        // call invalidate (main function)
        unit().invalidate(command.getClass());

        // assert that both calls are invalidated
        Assert.assertNull(unit().getCachedResult(command, call));
        Assert.assertNull(unit().getCachedResult(command, secondCall));

        verifyAllMocks();
    }

    @Test
    public void invalidateSingle() {
        if (canNotInvalidate()) {
            return;
        }

        // set cached results
        unit().setCachedResult(command, call, decision, result);
        unit().setCachedResult(command, secondCall, decision, secondResult);

        // invalidate with predicate
        unit().invalidate(command.getClass(), new Predicate<CacheKey>() {
            @Override
            public boolean apply(@Nullable CacheKey input) {
                return input != null && input.getArguments().getInt("account_id") == 5;
            }
        });

        // assert that only the first call was invalidated
        Assert.assertNull(unit().getCachedResult(command, call));
        Assert.assertEquals(secondResult, unit().getCachedResult(command, secondCall));

        verifyAllMocks();
    }

}

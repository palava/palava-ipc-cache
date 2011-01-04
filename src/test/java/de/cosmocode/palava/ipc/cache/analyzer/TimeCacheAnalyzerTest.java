package de.cosmocode.palava.ipc.cache.analyzer;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.CacheDecision;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Simple test for {@link TimeCacheAnalyzer}.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
public class TimeCacheAnalyzerTest implements UnitProvider<TimeCacheAnalyzer> {

    @Override
    public TimeCacheAnalyzer unit() {
        return new TimeCacheAnalyzer();
    }

    @Test
    public void analyze() {
        // create dummy call and command (should not be parsed by analyzer)
        final IpcCommand command = EasyMock.createMock("command", IpcCommand.class);
        final IpcCall call = EasyMock.createMock("call", IpcCall.class);
        EasyMock.replay(command, call);

        // annotation
        final TimeCached annotation = EasyMock.createMock(TimeCached.class);
        EasyMock.expect(annotation.lifeTime()).andReturn(2L);
        EasyMock.expect(annotation.lifeTimeUnit()).andReturn(TimeUnit.HOURS);
        EasyMock.expect(annotation.idleTime()).andReturn(20L);
        EasyMock.expect(annotation.idleTimeUnit()).andReturn(TimeUnit.MINUTES);
        EasyMock.replay(annotation);

        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(2L, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.HOURS, decision.getLifeTimeUnit());
        Assert.assertEquals(20L, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getIdleTimeUnit());
        EasyMock.verify(annotation);
    }

}

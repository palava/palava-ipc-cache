package de.cosmocode.palava.ipc.cache.analyzer;

import de.cosmocode.palava.ipc.cache.CacheDecision;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * <p></p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
public class CaseCacheAnalyzerModeAnyTest extends AbstractCaseCacheAnalyzerModeTest {

    @Override
    protected CaseCacheMode mode() {
        return CaseCacheMode.ANY;
    }

    @Test
    public void dateMatches() {
        arguments.put("date", new Date());
        final CacheDecision decision = unit().analyze(annotation, call, command);
        Assert.assertTrue(decision.shouldCache());
    }

    @Test
    public void accountIdMatches() {
        arguments.put("date", new Date());
        final CacheDecision decision = unit().analyze(annotation, call, command);
        Assert.assertTrue(decision.shouldCache());
    }

    @Test
    public void bothMatch() {
        arguments.put("account_id", 5);
        arguments.put("date", new Date());
        final CacheDecision decision = unit().analyze(annotation, call, command);
        Assert.assertTrue(decision.shouldCache());
    }

    @Test
    public void noneMatches() {
        arguments.put("anything", "bla");
        final CacheDecision decision = unit().analyze(annotation, call, command);
        Assert.assertFalse(decision.shouldCache());
    }

}

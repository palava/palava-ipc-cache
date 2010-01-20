package de.cosmocode.palava.command.cache;

import de.cosmocode.palava.core.call.filter.definition.Matchers;
import de.cosmocode.palava.core.inject.AbstractApplicationModule;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public final class CacheModule extends AbstractApplicationModule {

    @Override
    protected void configureApplication() {
        filter(Matchers.annotatedWith(Cache.class)).through(CacheFilter.class);
    }

}

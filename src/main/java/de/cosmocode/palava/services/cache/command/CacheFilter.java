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

package de.cosmocode.palava.services.cache.command;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.bridge.Content;
import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.call.filter.Filter;
import de.cosmocode.palava.bridge.call.filter.FilterChain;
import de.cosmocode.palava.bridge.call.filter.FilterException;
import de.cosmocode.palava.bridge.command.Commands;
import de.cosmocode.palava.bridge.session.HttpSession;
import de.cosmocode.palava.services.cache.CacheService;

/**
 * A {@link Filter} which intercepts command execution
 * to provide caching.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class CacheFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CacheFilter.class);

    private final CacheService service;
    
    @Inject
    public CacheFilter(CacheService service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }
    
    @Override
    public Content filter(Call call, FilterChain chain) throws FilterException {
        final Cache annotation = Commands.getClass(call.getCommand()).getAnnotation(Cache.class);
        
        switch (annotation.cachePolicy()) {
            case STATIC: {
                return cacheStatic(call, chain);
            }
            case SMART: {
                return cacheSmart(call, chain);
            }
            default: {
                throw new IllegalArgumentException("Unknown cache policy " + annotation.cachePolicy());
            }
        }
    }
    
    private Content cacheStatic(Call call, FilterChain chain) throws FilterException {
        final Class<?> type = Commands.getClass(call.getCommand());
        final Content cached = service.read(type);
        if (cached == null) {
            final Content content = chain.filter(call);
            log.debug("Caching content from {} statically", type);
            service.store(type, content);
            return content;
        } else {
            log.debug("Found statically cached content for {}", type);
            return cached;
        }
    }
    
    private Content cacheSmart(Call call, FilterChain chain) throws FilterException {
        final Class<?> commandClass = Commands.getClass(call.getCommand());
        final HttpSession session = call.getHttpRequest().getHttpSession();
        final Locale locale = session == null ? null : session.getLocale();
        final Object arguments = null;
        
        throw new UnsupportedOperationException(CachePolicy.SMART.name());
    }
    
}

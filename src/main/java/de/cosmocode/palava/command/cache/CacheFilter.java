/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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

package de.cosmocode.palava.command.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.filter.Filter;
import de.cosmocode.palava.core.call.filter.FilterChain;
import de.cosmocode.palava.core.call.filter.FilterException;
import de.cosmocode.palava.core.protocol.content.Content;
import de.cosmocode.palava.services.cache.CacheService;

/**
 * A {@link Filter} which intercepts command execution
 * to provide caching.
 *
 * @author Willi Schoenborn
 */
final class CacheFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CacheFilter.class);

    private final CacheService service;
    
    @Inject
    public CacheFilter(CacheService service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }
    
    @Override
    public Content filter(Call call, FilterChain chain) throws FilterException {
        final Cache annotation = call.getCommand().getClass().getAnnotation(Cache.class);
        
        if (annotation == null) {
            return chain.filter(call);
        } else if (annotation.cachePolicy() == CachePolicy.STATIC) {
            final Content cached = service.read(call.getClass());
            if (cached == null) {
                final Content content = chain.filter(call);
                log.debug("Caching content from {} statically", call.getClass());
                service.store(call.getClass(), content);
                return content;
            } else {
                log.debug("Found statically cached content for {}", call.getClass());
                return cached;
            }
        } else if (annotation.cachePolicy() == CachePolicy.SMART) {
            throw new UnsupportedOperationException(CachePolicy.SMART.name());
        } else {
            throw new IllegalArgumentException("Unknown cache policy " + annotation.cachePolicy());
        }
    }
    
}

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

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.json.JSONArray;

import de.cosmocode.palava.bridge.Content;
import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.call.filter.FilterChain;
import de.cosmocode.palava.bridge.call.filter.FilterException;
import de.cosmocode.palava.bridge.command.Command;
import de.cosmocode.palava.bridge.command.CommandException;
import de.cosmocode.palava.bridge.content.JsonContent;
import de.cosmocode.palava.bridge.request.HttpRequest;
import de.cosmocode.palava.bridge.session.HttpSession;

/**
 * An abstract test that provides some mock methods and a method that returns random content.
 * This class contains no tests itself.
 * 
 * @author Oliver Lorenz
 */
public abstract class AbstractCacheTest {

    public static final String EXPECT_CACHED = "should have been cached, but was not.";
    public static final String EXPECT_NOT_CACHED = "was illegally cached.";
    
    /**
     * Asserts that content1 is equal to content2.
     * That means that the second call returned the same result as the first,
     * which means that the first call was cached.
     * @param content1 content from first call
     * @param content2 content from second call
     */
    protected void assertCached(final Content content1, final Content content2) {
        Assert.assertSame(EXPECT_CACHED, content1, content2);
    }
    
    /**
     * Asserts that content1 is not equal to content2.
     * That means that the second call returned a different result than the first,
     * which means that the first call was not cached.
     * @param content1 content from first call
     * @param content2 content from second call
     */
    protected void assertNotCached(final Content content1, final Content content2) {
        Assert.assertNotSame(EXPECT_NOT_CACHED, content1, content2);
    }
    
    /**
     * Get the {@link CacheFilter} implementation to call on {@link #filterAndExecute(Call)}.
     * @return a CacheFilter
     */
    protected abstract CacheFilter getFilter();
    
    /**
     * Returns random content on every invocation.
     * @return a Json-array of a random number between 0 and 10000
     */
    protected Content randomContent() {
        final JSONArray array = new JSONArray();
        array.put(Math.round(Math.random() * 10000));
        return new JsonContent(array);
    }
    
    /**
     * Filters the given call and returns either the filtered content or a {@link #randomContent()}.
     * @param call the call to filter on
     * @return a {@link #randomContent()} or the cached Content from {@link #getFilter()}
     * @throws FilterException if the filter given by {@link #getFilter()} throws it
     */
    protected Content filterAndExecute(final Call call) throws FilterException {
        final FilterChain chain = EasyMock.createMock(FilterChain.class);
        EasyMock.expect(chain.filter(call)).andReturn(randomContent());
        EasyMock.replay(chain);
        return getFilter().filter(call, chain);
    }
    
    /**
     * Creates a {@link SimpleCall} from the given parameters.
     * Then it filters that call and returns either the filtered content or a {@link #randomContent()}.
     * @param command a command class; not called, just passed to the filter
     * @param request an {@link HttpRequest} that gets passed to SimpleCall
     * @param arguments several String arguments in (key, value, key, value, ...) order
     * @return a {@link #randomContent()} or the cached Content from {@link #getFilter()}
     * @throws FilterException if the filter given by {@link #getFilter()} throws it
     */
    protected Content filterAndExecute(final Command command, final HttpRequest request, final String... arguments)
        throws FilterException {
        
        return filterAndExecute(new SimpleCall(command, request, arguments));
    }
    
    /**
     * Mocks up an HttpSession. It only has the {@link HttpSession#getLocale()} set.
     * @param locale a locale to pass to the session
     * @return a mocked up {@link HttpSession}
     */
    protected HttpSession mockSession(final Locale locale) {
        final HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(session.getLocale()).andStubReturn(locale);
        EasyMock.replay(session);
        
        return session;
    }
    
    /**
     * Mocks up an HttpRequest. It only has the {@link HttpRequest#getHttpSession()} set.
     * @param session a session to pass to the request
     * @return a mocked up {@link HttpRequest}
     */
    protected HttpRequest mockRequestForSession(final HttpSession session) {
        final HttpRequest request = EasyMock.createMock(HttpRequest.class);
        EasyMock.expect(request.getHttpSession()).andStubReturn(session);
        EasyMock.replay(request);
        
        return request;
    }
    
    /**
     * Mocks up an HttpRequest. It only has the {@link HttpRequest#getHttpSession()} set,
     * which in turn only has the {@link HttpSession#getLocale()} method set.
     * @param locale a locale to pass to the session of the request
     * @return a mocked up {@link HttpRequest}
     */
    protected HttpRequest mockRequest(final Locale locale) {
        return mockRequestForSession(mockSession(locale));
    }
    
    /**
     * A dummy command that returns a {@link AbstractCacheTest#randomContent()} on every execution.
     * 
     * @author Oliver Lorenz
     */
    protected abstract class DummyCommand implements Command {
        
        @Override
        public Content execute(Call call) throws CommandException {
            return randomContent();
        }
        
    }

}

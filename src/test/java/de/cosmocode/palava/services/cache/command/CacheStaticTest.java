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

import org.junit.Test;

import de.cosmocode.palava.bridge.Content;
import de.cosmocode.palava.bridge.call.filter.FilterException;
import de.cosmocode.palava.bridge.command.Command;
import de.cosmocode.palava.bridge.request.HttpRequest;
import de.cosmocode.palava.bridge.session.HttpSession;

/**
 * Tests the {@link CacheFilter} on some Commands with the {@code @Cache} annotation 
 * and the cachePolicy set to {@link CachePolicy#STATIC}.
 * 
 * @author Oliver Lorenz
 */
public final class CacheStaticTest extends AbstractCacheTest {
    
    private final CacheFilter filter;
    private final Command command;
    
    private final Command namedCommand1;
    private final Command namedCommand2;
    
    
    public CacheStaticTest() {
        this.filter = new CacheFilter(new SimpleCacheService());
        this.command = new SmartCacheCommand();
        this.namedCommand1 = new NamedCommand1();
        this.namedCommand2 = new NamedCommand2();
    }
    
    
    @Override
    protected CacheFilter getFilter() {
        return filter;
    }
    
    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.STATIC)}. */
    @Cache(cachePolicy = CachePolicy.STATIC)
    protected class SmartCacheCommand extends DummyCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.STATIC)}. */
    @Cache(cachePolicy = CachePolicy.STATIC)
    protected class NamedCommand1 extends DummyCommand { }

    /** A dummy command with the annotation {@code @Cache(cachePolicy = CachePolicy.STATIC)}. */
    @Cache(cachePolicy = CachePolicy.STATIC)
    protected class NamedCommand2 extends DummyCommand { }
    
    
    /**
     * Tests the CacheFilter with same arguments in the same request.
     * @throws FilterException should not happen
     */
    @Test
    public void testSameArgumentsSameRequest() throws FilterException {
        final HttpRequest request1 = mockRequest(Locale.GERMAN);
        
        final Content content1 = filterAndExecute(command, request1, "arg1", "value1", "arg2", "value2");
        final Content content2 = filterAndExecute(command, request1, "arg1", "value1", "arg2", "value2");
        
        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter with different arguments in the same request.
     * @throws FilterException should not happen
     */
    @Test
    public void testDifferentArgumentsSameRequest() throws FilterException {
        final HttpRequest request1 = mockRequest(Locale.GERMAN);
        
        final Content content1 = filterAndExecute(command, request1, "arg1", "value1");
        final Content content2 = filterAndExecute(command, request1, "arg2", "value2", "arg3", "value3");
        
        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter with no arguments in the same request.
     * @throws FilterException should not happen
     */
    @Test
    public void testNoArgumentsSameRequest() throws FilterException {
        final HttpRequest request1 = mockRequest(Locale.GERMAN);
        
        final Content content1 = filterAndExecute(command, request1);
        final Content content2 = filterAndExecute(command, request1);
        
        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter on a request with no locale set.
     * @throws FilterException should not happen
     */
    @Test
    public void testNoLocaleSameRequest() throws FilterException {
        final HttpRequest request = mockRequest(null);
        
        final Content content1 = filterAndExecute(command, request, "arg1", "value1", "arg2", "value2");
        final Content content2 = filterAndExecute(command, request, "arg1", "value1", "arg2", "value2");
        
        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter on 2 requests with no locale set.
     * @throws FilterException should not happen
     */
    @Test
    public void testNoLocaleDifferentRequest() throws FilterException {
        final HttpRequest request1 = mockRequest(null);
        final HttpRequest request2 = mockRequest(null);
        
        final Content content1 = filterAndExecute(command, request1, "arg1", "value1", "arg2", "value2");
        final Content content2 = filterAndExecute(command, request2, "arg1", "value1", "arg2", "value2");
        
        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter in 2 requests with the same session and the same arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testSameArgumentsDifferentRequestSameLocale() throws FilterException {
        final HttpSession session = mockSession(Locale.GERMAN);
        
        final HttpRequest request1 = mockRequestForSession(session);
        final HttpRequest request2 = mockRequestForSession(session);
        
        final Content content1 = filterAndExecute(command, request1, "arg1", "value1", "arg2", "value2");
        final Content content2 = filterAndExecute(command, request2, "arg1", "value1", "arg2", "value2");
        
        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter on 2 requests with different locales, but the same arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testSameArgumentsDifferentRequestDifferentLocale() throws FilterException {
        final HttpRequest request1 = mockRequest(Locale.GERMAN);
        final HttpRequest request2 = mockRequest(Locale.ENGLISH);
        
        final Content content1 = filterAndExecute(command, request1, "arg1", "value1", "arg2", "value2");
        final Content content2 = filterAndExecute(command, request2, "arg1", "value1", "arg2", "value2");
        
        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter in 2 requests with the same session and no arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testNoArgumentsDifferentRequestSameLocale() throws FilterException {
        final HttpSession session = mockSession(Locale.GERMAN);
        
        final HttpRequest request1 = mockRequestForSession(session);
        final HttpRequest request2 = mockRequestForSession(session);
        
        final Content content1 = filterAndExecute(command, request1);
        final Content content2 = filterAndExecute(command, request2);

        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter on 2 requests with different locales and no arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testNoArgumentsDifferentRequestDifferentLocale() throws FilterException {
        final HttpRequest request1 = mockRequest(Locale.GERMAN);
        final HttpRequest request2 = mockRequest(Locale.ENGLISH);
        
        final Content content1 = filterAndExecute(command, request1);
        final Content content2 = filterAndExecute(command, request2);

        assertCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter on a request with different command names but the same arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testDifferentNameSameArgumentsSameRequest() throws FilterException {
        final HttpRequest request = mockRequest(Locale.GERMAN);
        
        final Content content1 = filterAndExecute(namedCommand1, request, "arg1", "value1", "arg2", "value2");
        final Content content2 = filterAndExecute(namedCommand2, request, "arg1", "value1", "arg2", "value2");

        assertNotCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter on a request with different command names and different arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testDifferentNameDifferentArgumentsSameRequest() throws FilterException {
        final HttpRequest request = mockRequest(Locale.GERMAN);
        
        final Content content1 = filterAndExecute(namedCommand1, request, "key1", "value1");
        final Content content2 = filterAndExecute(namedCommand2, request, "arg1", "value1", "arg2", "value2");

        assertNotCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter in 2 requests with the same session, different command names and the same arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testDifferentNameSameArgumentsDifferentRequestSameLocale() throws FilterException {
        final HttpSession session = mockSession(Locale.GERMAN);
        
        final HttpRequest request1 = mockRequestForSession(session);
        final HttpRequest request2 = mockRequestForSession(session);
        
        final Content content1 = filterAndExecute(namedCommand1, request1, "arg1", "value1", "arg2", "value2");
        final Content content2 = filterAndExecute(namedCommand2, request2, "arg1", "value1", "arg2", "value2");

        assertNotCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter in 2 requests with the same session, different command names and the same arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testDifferentNameSameArgumentsDifferentRequestDifferentLocale() throws FilterException {
        final HttpRequest request1 = mockRequest(Locale.GERMAN);
        final HttpRequest request2 = mockRequest(Locale.ENGLISH);
        
        final Content content1 = filterAndExecute(namedCommand1, request1, "arg1", "value1", "arg2", "value2");
        final Content content2 = filterAndExecute(namedCommand2, request2, "arg1", "value1", "arg2", "value2");

        assertNotCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter in 2 requests with the same session, different command names and different arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testDifferentNameDifferentArgumentsDifferentRequestSameLocale() throws FilterException {
        final HttpSession session = mockSession(Locale.GERMAN);
        
        final HttpRequest request1 = mockRequestForSession(session);
        final HttpRequest request2 = mockRequestForSession(session);
        
        final Content content1 = filterAndExecute(namedCommand1, request1, "blubb", "bla", "arg2", "value2");
        final Content content2 = filterAndExecute(namedCommand2, request2, "hui", "bu", "foo", "bar");

        assertNotCached(content1, content2);
    }
    
    /**
     * Tests the CacheFilter in 2 requests with the same session, different command names and different arguments.
     * @throws FilterException should not happen
     */
    @Test
    public void testDifferentNameDifferentArgumentsDifferentRequestDifferentLocale() throws FilterException {
        final HttpRequest request1 = mockRequest(Locale.GERMAN);
        final HttpRequest request2 = mockRequest(Locale.ENGLISH);
        
        final Content content1 = filterAndExecute(namedCommand1, request1, "blubb", "bla", "arg2", "value2");
        final Content content2 = filterAndExecute(namedCommand2, request2, "hui", "bu", "foo", "bar");

        assertNotCached(content1, content2);
    }
    
}

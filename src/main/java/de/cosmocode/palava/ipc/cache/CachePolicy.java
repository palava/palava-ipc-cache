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

package de.cosmocode.palava.ipc.cache;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * A CachePolicy decides how to cache an {@link IpcCommand}.
 * 
 * @author Willi Schoenborn
 * @author Oliver Lorenz
 */
public enum CachePolicy {

    /**
     * TODO update
     * 
     * <p> A CachePolicy where the fully qualified name of the command-class,
     * the {@linkplain IpcCall#getArguments() Arguments} of a Call
     * and the {@linkplain HttpSession#getLocale() Locale} of the Session of a Call
     * are taken into consideration for caching.
     * </p>
     * <h4>This policy behaves differently if nulls occur: </h4>
     * <ul>
     *   <li> If the locale is null, then no caching occurs. </li> 
     *   <li> If the arguments are null, then they are ignored 
     *        and only the locale and the fully qualified name are taken for caching </li>
     * </ul>
     */
    SMART,
    
    /**
     * <p> A CachePolicy where only the fully qualified name of the command-class
     * is used for caching.
     * TODO + arguments
     * </p>
     * <p> This means that the Command annotated with this CachePolicy
     * always returns the same Content, independent of the call.
     * </p>
     */
    STATIC;
    
}

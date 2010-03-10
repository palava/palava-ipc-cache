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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to declare a cachable state.
 *
 * @author Willi Schoenborn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cache {

    CachePolicy policy() default CachePolicy.SMART;
    
    /**
     * <p> The maximum age of a cached command.
     * The time unit is given by {@link #maxAgeUnit()}.
     * </p>
     * <p> A non-positive value (i.e. <= 0) means that every command is cached eternally.
     * The default is 0, which means that every command lives eternally by default.
     * </p>
     * @return the maximum age of every command in {@link #maxAgeUnit()}
     */
    long maxAge() default 0;
    
    /**
     * <p> The time unit for {@link #maxAge()}.
     * Defaults to {@link TimeUnit#MINUTES}.
     * </p>
     * 
     * @return the time unit for {@link #maxAge()}
     */
    TimeUnit maxAgeUnit() default TimeUnit.MINUTES;
    
}

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

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: olor
 * Date: 04.01.11
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */
public interface CacheDecision {

    // TODO Documentation

    /**
     * Whether the command result should be cached or not.
     * @return true if the result should be cached, false otherwise
     */
    boolean shouldCache();

    long getLifeTime();

    TimeUnit getLifeTimeUnit();

    long getIdleTime();

    TimeUnit getIdleTimeUnit();

}

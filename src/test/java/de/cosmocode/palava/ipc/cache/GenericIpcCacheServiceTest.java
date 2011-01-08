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

import org.junit.After;
import org.junit.Before;

import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.core.lifecycle.Startable;

/**
 * <p>
 * Tests {@link GenericIpcCacheService}.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public class GenericIpcCacheServiceTest extends AbstractIpcCacheServiceTest implements Startable {

    private final Framework framework = Palava.newFramework();

    @Before
    @Override
    public void start() {
        framework.start();
    }

    @After
    @Override
    public void stop() {
        framework.stop();
    }

    @Override
    public IpcCacheService unit() {
        return framework.getInstance(GenericIpcCacheService.class);
    }

    @Override
    protected boolean supportsInvalidate() {
        return false;
    }

}

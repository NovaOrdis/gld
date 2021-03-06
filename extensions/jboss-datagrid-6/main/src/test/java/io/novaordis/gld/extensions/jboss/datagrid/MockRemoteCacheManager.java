/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.extensions.jboss.datagrid;

import io.novaordis.utilities.NotYetImplementedException;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/11/17
 */
public class MockRemoteCacheManager extends RemoteCacheManager {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String DEFAULT_CACHE_NAME = "n46b_3hgaFG3";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;
    private Map<String, MockRemoteCache> caches;

    private Configuration infinispanConfiguration;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockRemoteCacheManager(Configuration infinispanConfiguration) {

        this.started = false;
        this.caches = new HashMap<>();
        this.caches.put(DEFAULT_CACHE_NAME, new MockRemoteCache(DEFAULT_CACHE_NAME, this));
        this.infinispanConfiguration = infinispanConfiguration;
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public RemoteCache getCache() {

        return caches.get(DEFAULT_CACHE_NAME);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RemoteCache getCache(String cacheName) {

        MockRemoteCache mc = caches.get(cacheName);

        if (mc == null) {

            throw new NotYetImplementedException("DON'T KNOW WHAT EXCEPTION TO THROW FOR A CACHE THAT DOES NOT EXIST");
        }

        return mc;
    }

    @Override
    public void start() {

        if (started) {

            return;
        }

        if (caches != null) {

            for (MockRemoteCache mc : caches.values()) {

                mc.start();
                assertTrue(mc.isStarted());
            }
        }


        this.started = true;
    }

    @Override
    public void stop() {

        if (!started) {

            return;
        }

        //noinspection Convert2streamapi
        for(MockRemoteCache mc: caches.values()) {

            mc.stop();
        }

        this.started = false;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Configuration getConfiguration() {

        return infinispanConfiguration;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

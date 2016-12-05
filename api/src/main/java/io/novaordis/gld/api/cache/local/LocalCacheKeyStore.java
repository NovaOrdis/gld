/*
 * Copyright (c) 2016 Nova Ordis LLC
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

package io.novaordis.gld.api.cache.local;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.LoadDriver;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class LocalCacheKeyStore implements KeyStore {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private LoadDriver loadDriver;
    private boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    public LocalCacheKeyStore(LoadDriver loadDriver) {

        this.loadDriver = loadDriver;
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void start() throws Exception {

        if (started) {
            return;
        }

        started = true;
    }

    @Override
    public void stop() throws Exception {

        if (!started) {
            return;
        }

        started = false;
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    @Override
    public boolean isReadOnly() {
        throw new RuntimeException("isReadOnly() NOT YET IMPLEMENTED");
    }

    @Override
    public void store(String key) throws Exception {
        throw new RuntimeException("store() NOT YET IMPLEMENTED");
    }

    @Override
    public String get() {
        throw new RuntimeException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public LoadDriver getLoadDriver() {

        return loadDriver;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

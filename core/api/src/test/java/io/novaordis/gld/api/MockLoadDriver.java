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

package io.novaordis.gld.api;

import io.novaordis.gld.api.configuration.Configuration;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.sampler.Sampler;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class MockLoadDriver implements LoadDriver {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadDriver implementation ---------------------------------------------------------------------------------------

    @Override
    public String getID() {
        throw new RuntimeException("getID() NOT YET IMPLEMENTED");
    }

    @Override
    public void init(Configuration c) throws Exception {
        throw new RuntimeException("init() NOT YET IMPLEMENTED");
    }

    @Override
    public void run() {
        throw new RuntimeException("run() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean background() {
        throw new RuntimeException("background() NOT YET IMPLEMENTED");
    }

    @Override
    public Service getService() {
        throw new RuntimeException("getService() NOT YET IMPLEMENTED");
    }

    @Override
    public KeyStore getKeyStore() {
        throw new RuntimeException("getKeyStore() NOT YET IMPLEMENTED");
    }

    @Override
    public Runner getRunner() {
        throw new RuntimeException("getRunner() NOT YET IMPLEMENTED");
    }

    @Override
    public Sampler getSampler() {
        throw new RuntimeException("getSampler() NOT YET IMPLEMENTED");
    }

    @Override
    public void error(Throwable t) {
        throw new RuntimeException("error() NOT YET IMPLEMENTED");
    }

    @Override
    public void error(String msg) {
        throw new RuntimeException("error() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

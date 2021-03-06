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

package io.novaordis.gld.extensions.mock;

import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/21/16
 */
public class MockService implements Service {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    private static boolean failToInstantiate;
    private static String version;

    static {

        failToInstantiate = false;
        version = null;
    }

    public static void configureToFailToInstantiate() {

        failToInstantiate = true;
    }

    public static void reset() {

        failToInstantiate = false;
        version = null;
    }

    public static void setVersion(String s) {

        version = s;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockService() {

        if (failToInstantiate) {

            throw new RuntimeException("SYNTHETIC");
        }
    }

    // Service implementation // Constructors --------------------------------------------------------------------------

    @Override
    public LoadDriver getLoadDriver() {
        throw new NotYetImplementedException("getLoadDriver() NOT YET IMPLEMENTED");
    }

    @Override
    public void setLoadDriver(LoadDriver d) {
        throw new NotYetImplementedException("setLoadDriver() NOT YET IMPLEMENTED");
    }

    @Override
    public LoadStrategy getLoadStrategy() {
        throw new NotYetImplementedException("getLoadStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public void setLoadStrategy(LoadStrategy s) {
        throw new NotYetImplementedException("setLoadStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {
        throw new NotYetImplementedException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws Exception {
        throw new NotYetImplementedException("start() NOT YET IMPLEMENTED");
    }

    @Override
    public void stop() throws Exception {
        throw new NotYetImplementedException("stop() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isStarted() {
        throw new NotYetImplementedException("isStarted() NOT YET IMPLEMENTED");
    }

    @Override
    public ServiceType getType() {
        throw new NotYetImplementedException("getType() NOT YET IMPLEMENTED");
    }

    @Override
    public String getVersion() {

        return version;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

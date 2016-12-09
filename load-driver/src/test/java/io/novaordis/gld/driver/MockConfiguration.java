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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.Configuration;
import io.novaordis.gld.api.LoadConfiguration;
import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.gld.api.StoreConfiguration;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class MockConfiguration implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Configuration implementation ------------------------------------------------------------------------------------

    @Override
    public ServiceConfiguration getServiceConfiguration() {
        throw new RuntimeException("getServiceConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public LoadConfiguration getLoadConfiguration() {
        throw new RuntimeException("getLoadConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public StoreConfiguration getStoreConfiguration() {
        throw new RuntimeException("getStoreConfiguration() NOT YET IMPLEMENTED");
    }


    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

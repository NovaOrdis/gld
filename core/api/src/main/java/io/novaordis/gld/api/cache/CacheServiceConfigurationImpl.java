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

package io.novaordis.gld.api.cache;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfigurationImpl;

import java.io.File;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class CacheServiceConfigurationImpl extends ServiceConfigurationImpl implements CacheServiceConfiguration  {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public CacheServiceConfigurationImpl(Map<String, Object> rawConfiguration, File configurationDirectory)
            throws Exception {

        super(rawConfiguration, configurationDirectory);
    }

    // CacheServiceConfiguration implementation ------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        Object o = get(Object.class, ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL);

        if (o == null) {
            return "null";
        }

        return o.toString();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

package io.novaordis.gld.api.configuration;

import io.novaordis.gld.api.Configuration;
import io.novaordis.gld.api.LoadDriverConfiguration;
import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class YamlBasedConfiguration implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String SERVICE_SECTION_LABEL = "service";
    public static final String LOAD_SECTION_LABEL = "load";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ServiceConfiguration serviceConfiguration;
    private LoadDriverConfiguration loadDriverConfiguration;

    // Constructors ----------------------------------------------------------------------------------------------------

    public YamlBasedConfiguration(File file) throws Exception {

        parse(file);

    }

    // Configuration implementation ------------------------------------------------------------------------------------

    @Override
    public ServiceConfiguration getServiceConfiguration() {

        return serviceConfiguration;
    }

    @Override
    public LoadDriverConfiguration getLoadDriverConfiguration() {

        return loadDriverConfiguration;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void parse(File file) throws Exception {

        InputStream is = null;

        try {

            is = new BufferedInputStream(new FileInputStream(file));

            Yaml yaml = new Yaml();

            Map topLevelConfigurationMap = (Map)yaml.load(is);
            Map serviceConfigurationMap = null;
            Map loadConfigurationMap = null;

            if (topLevelConfigurationMap != null) {

                serviceConfigurationMap = (Map) topLevelConfigurationMap.get(SERVICE_SECTION_LABEL);
                loadConfigurationMap = (Map) topLevelConfigurationMap.get(LOAD_SECTION_LABEL);
            }

            if (serviceConfigurationMap == null) {

                throw new UserErrorException(
                        "'" + SERVICE_SECTION_LABEL + "' section empty or missing from configuration file " + file);
            }

            serviceConfiguration = new ServiceConfigurationBase(serviceConfigurationMap);

            if (loadConfigurationMap == null) {

                throw new UserErrorException(
                        "'" + LOAD_SECTION_LABEL + "' section empty or missing from configuration file " + file);
            }

            loadDriverConfiguration = new LoadDriverConfigurationImpl(loadConfigurationMap);
        }
        finally {

            if (is != null) {

                is.close();
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}

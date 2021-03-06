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

package io.novaordis.gld.api.mock.configuration;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.configuration.ImplementationConfiguration;
import io.novaordis.gld.api.configuration.ImplementationConfigurationImpl;
import io.novaordis.gld.api.configuration.LowLevelConfigurationBase;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.mock.MockService;
import io.novaordis.gld.api.mock.load.MockLdLoadStrategyFactory;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class MockServiceConfiguration extends LowLevelConfigurationBase implements ServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockServiceConfiguration() {

        super(new HashMap<>(), new File("."));

        //
        // make sure this service configuration "encourages" the use of the load-driver own LoadStrategyFactory
        //

        Map<String, Object> loadStrategyConfig = new HashMap<>();
        loadStrategyConfig.put(LoadStrategy.FACTORY_CLASS_LABEL, MockLdLoadStrategyFactory.class.getName());
        set(loadStrategyConfig, LOAD_STRATEGY_CONFIGURATION_LABEL);
    }


    // ServiceConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public ServiceType getType() throws UserErrorException {

        return ServiceType.mock;
    }

    @Override
    public ImplementationConfiguration getImplementationConfiguration() throws UserErrorException {

        Map<String, Object> map = new HashMap<>();
        map.put(ImplementationConfiguration.EXTENSION_CLASS_LABEL, MockService.class.getName());
        return new ImplementationConfigurationImpl(map, new File("."));
    }

    @Override
    public String getLoadStrategyName() throws UserErrorException {
        throw new RuntimeException("getLoadStrategyName() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

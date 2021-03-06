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

package io.novaordis.gld.api.mock.load;

import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.LoadStrategyFactory;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;

/**
 * Named MockLdLoadStrategyFactory instead of MockLoadStrategyFactory to avoid non-obviously clashes with the API
 * MockLoadStrategy. Ran into problems at times when debugging the modules together.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
// will be instantiated by reflection
@SuppressWarnings("unused")
public class MockLdLoadStrategyFactory implements LoadStrategyFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategyFactory implementation ------------------------------------------------------------------------------

    @Override
    public MockLdLoadStrategy buildInstance(ServiceConfiguration sc, LoadConfiguration lc)
            throws Exception {

        MockLdLoadStrategy ms = new MockLdLoadStrategy();
        ms.init(sc, lc);
        return ms;
    }

    @Override
    public ServiceType getServiceType() {

        return ServiceType.mock;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "load-driver Mock(Ld)LoadStrategyFactory";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

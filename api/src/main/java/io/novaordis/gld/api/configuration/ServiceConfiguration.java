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

import io.novaordis.gld.api.ServiceType;
import io.novaordis.utilities.UserErrorException;

import java.util.Map;

/**
 * Typed and untyped access to underlying service configuration.
 *
 * The implementations of this interface also allow low-level typed access (typed access to specific points into the
 * configuration structure) via LowLevelConfiguration.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public interface ServiceConfiguration extends LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    int DEFAULT_KEY_SIZE = 12; // in characters

    String TYPE_LABEL = "type";
    String IMPLEMENTATION_LABEL = "implementation";
    String LOAD_STRATEGY_CONFIGURATION_LABEL = "load-strategy";
    String LOAD_STRATEGY_NAME_LABEL = "name";

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Typed Access ----------------------------------------------------------------------------------------------------

    ServiceType getType() throws UserErrorException;

    String getImplementation() throws UserErrorException;

    String getLoadStrategyName() throws UserErrorException;

}

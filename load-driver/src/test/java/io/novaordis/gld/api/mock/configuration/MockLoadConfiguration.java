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

import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class MockLoadConfiguration implements LoadConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // MockLoadConfiguration implementation ----------------------------------------------------------------------------

    @Override
    public int getThreadCount() throws UserErrorException {

        return 1;
    }

    @Override
    public Long getOperations() throws UserErrorException {
        throw new RuntimeException("getOperations() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getRequests() throws UserErrorException {
        throw new RuntimeException("getRequests() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getMessages() throws UserErrorException {
        throw new RuntimeException("getMessages() NOT YET IMPLEMENTED");
    }

    @Override
    public <T> T get(Class<? extends T> type, String... path) {
        throw new RuntimeException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public File getFile(String... path) {
        throw new RuntimeException("getFile() NOT YET IMPLEMENTED");
    }

    @Override
    public Map<String, Object> get(String... path) {
        throw new RuntimeException("get() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
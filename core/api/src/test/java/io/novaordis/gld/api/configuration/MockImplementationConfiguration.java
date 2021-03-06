/*
 * Copyright (c) 2017 Nova Ordis LLC
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

import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/10/17
 */
public class MockImplementationConfiguration implements ImplementationConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String extensionName;
    private String extensionClass;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockImplementationConfiguration() {

        this.extensionName = null;
        this.extensionClass = null;
    }

    // ImplementationConfiguration implementation ----------------------------------------------------------------------

    @Override
    public String getExtensionName() throws UserErrorException {

        return extensionName;
    }

    @Override
    public String getExtensionClass() throws UserErrorException {

        return extensionClass;
    }

    @Override
    public <T> T get(Class<? extends T> type, String... path) {
        throw new NotYetImplementedException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public <T> T remove(Class<? extends T> type, String... path) {
        throw new RuntimeException("remove() NOT YET IMPLEMENTED");
    }

    @Override
    public File getFile(String... path) {
        throw new NotYetImplementedException("getFile() NOT YET IMPLEMENTED");
    }

    @Override
    public Map<String, Object> get(String... path) {
        throw new NotYetImplementedException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public List<Object> getList(String... path) {
        throw new NotYetImplementedException("getList() NOT YET IMPLEMENTED");
    }

    @Override
    public File getConfigurationDirectory() {
        throw new NotYetImplementedException("getConfigurationDirectory() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setExtensionName(String s) {

        this.extensionName = s;
    }

    public void setExtensionClass(String s) {

        this.extensionClass = s;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

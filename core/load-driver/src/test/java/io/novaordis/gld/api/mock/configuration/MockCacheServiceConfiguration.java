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

import io.novaordis.gld.api.cache.CacheServiceConfiguration;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/14/16
 */
public class MockCacheServiceConfiguration extends MockServiceConfiguration implements CacheServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int keySize;
    private int valueSize;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockCacheServiceConfiguration() {

        this.keySize = 1;
        this.valueSize = 1;
    }

    // CacheServiceConfiguration implementation ------------------------------------------------------------------------

    @Override
    public int getKeySize() throws UserErrorException {

        return keySize;
    }

    @Override
    public int getValueSize() throws UserErrorException {

        return valueSize;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setKeySize(int i) {

        this.keySize = i;
    }

    public void setValueSize(int i) {

        this.valueSize = i;
    }

    //
    // make set() public for testing
    //

    @Override
    public void set(Object instance, String ... path) {
        super.set(instance, path);
    }

    //
    // make remove() public for testing
    //

    @Override
    public void remove(String ... path) {
        super.remove(path);
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
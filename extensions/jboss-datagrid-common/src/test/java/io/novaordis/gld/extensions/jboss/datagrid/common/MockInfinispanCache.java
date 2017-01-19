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

package io.novaordis.gld.extensions.jboss.datagrid.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public class MockInfinispanCache implements InfinispanCache {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String name;
    private Map<String, String> storage;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param name must be non-null.
     */
    public MockInfinispanCache(String name) {

        if (name == null) {

            throw new IllegalArgumentException("null cache name");
        }

        this.name = name;
        this.storage = new HashMap<>();
    }

    // InfinispanCache implementation ----------------------------------------------------------------------------------

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String get(String key) throws Exception {

        return storage.get(key);
    }

    @Override
    public void put(String key, String value) throws Exception {

        storage.put(key, value);
    }

    @Override
    public void remove(String key) throws Exception {

        storage.remove(key);
    }

    @Override
    public Set<String> keys() throws Exception {

        return storage.keySet();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

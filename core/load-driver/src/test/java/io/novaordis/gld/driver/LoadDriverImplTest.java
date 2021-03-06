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

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.mock.configuration.MockConfiguration;
import io.novaordis.gld.api.service.Service;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class LoadDriverImplTest extends LoadDriverTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void service_LoadStrategy_LoadDriver_Relationships() throws Exception {

        MockConfiguration mc = new MockConfiguration();

        LoadDriverImpl d = getLoadDriverToTest();

        d.init(mc);

        Service s = d.getService();

        assertEquals(d, s.getLoadDriver());

        LoadStrategy ls = s.getLoadStrategy();
        assertNotNull(ls);

        assertEquals(s, ls.getService());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected LoadDriverImpl getLoadDriverToTest() throws Exception {

        return new LoadDriverImpl("test", true);
    }
    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

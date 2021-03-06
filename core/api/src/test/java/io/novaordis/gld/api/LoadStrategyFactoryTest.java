/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api;

import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.mock.load.MockLoadStrategy;
import io.novaordis.gld.api.mock.load.MockLoadStrategyFactory;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class LoadStrategyFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LoadStrategyFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // inferFullyQualifiedLoadStrategyClassName() ----------------------------------------------------------------------

    @Test
    public void inferFullyQualifiedLoadStrategyClassName() throws Exception {

        String s = LoadStrategyFactory.inferFullyQualifiedLoadStrategyClassName(ServiceType.mock, "kcom");
        assertEquals("io.novaordis.gld.api.mock.load.KcomLoadStrategy", s);
    }

    // inferSimpleClassName() ------------------------------------------------------------------------------------------

    @Test
    public void inferSimpleClassName() throws Exception {

        String s = LoadStrategyFactory.inferSimpleClassName("write");
        assertEquals("WriteLoadStrategy", s);
    }

    @Test
    public void inferSimpleClassName_Dashes() throws Exception {

        String s = LoadStrategyFactory.inferSimpleClassName("read-then-write-on-miss");
        assertEquals("ReadThenWriteOnMissLoadStrategy", s);
    }

    @Test
    public void inferSimpleClassName_EndsInLoadStrategy() throws Exception {

        String s = LoadStrategyFactory.inferSimpleClassName("some-load-strategy");
        assertEquals("SomeLoadStrategy", s);
    }

    // build() static wrapper ------------------------------------------------------------------------------------------

    @Test
    public void build_FactoryFullyQualifiedClassName_NoSuchClass() throws Exception {

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockServiceConfiguration msc = new MockServiceConfiguration();

        Map<String, Object> completelyCustomLoadStrategyFactoryConfig = new HashMap<>();
        completelyCustomLoadStrategyFactoryConfig.put(
                LoadStrategy.FACTORY_CLASS_LABEL,
                "I.am.sure.this.class.does.not.exist");
        msc.set(completelyCustomLoadStrategyFactoryConfig ,ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        try {

            LoadStrategyFactory.build(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();

            assertTrue(msg.matches(
                    "failed to instantiate a load strategy factory corresponding to class I.am.sure.this.class.does.not.exist.*"));

            ClassNotFoundException e2 = (ClassNotFoundException)e.getCause();
            assertNotNull(e2);
        }
    }

    @Test
    public void build_FactoryFullyQualifiedClassName() throws Exception {

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockServiceConfiguration msc = new MockServiceConfiguration();

        Map<String, Object> completelyCustomLoadStrategyFactoryConfig = new HashMap<>();
        completelyCustomLoadStrategyFactoryConfig.put(
                LoadStrategy.FACTORY_CLASS_LABEL,
                MockLoadStrategyFactory.class.getName());
        msc.set(completelyCustomLoadStrategyFactoryConfig, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        MockLoadStrategy s = (MockLoadStrategy)LoadStrategyFactory.build(msc, mlc);
        assertNotNull(s);
    }

    @Test
    public void build_NullServiceType() throws Exception {

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockServiceConfiguration msc = new MockServiceConfiguration();
        msc.setServiceType(null);


        try {

            LoadStrategyFactory.build(msc, mlc);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            assertEquals("null service type", e.getMessage());
        }
    }

    @Test
    public void build_KnownServiceType() throws Exception {

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockServiceConfiguration msc = new MockServiceConfiguration();
        LoadStrategy s = LoadStrategyFactory.build(msc, mlc);
        assertTrue(s instanceof MockLoadStrategy);
    }

    @Test
    public void build_UnknownServiceType() throws Exception {

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockServiceConfiguration msc = new MockServiceConfiguration();
        msc.setServiceType(ServiceType.unknown);

        try {

            LoadStrategyFactory.build(msc, mlc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("failed to instantiate a load strategy factory"));
            assertTrue(msg.contains("'unknown' service type"));
            Throwable t = e.getCause();
            assertTrue(t instanceof ClassNotFoundException);
        }
    }

    // buildInstance() -------------------------------------------------------------------------------------------------

    @Test
    public void buildInstance() throws Exception {

        LoadStrategyFactory f = getLoadStrategyFactoryToTest();

        ServiceType t = f.getServiceType();

        ServiceConfiguration c = getCorrespondingConfigurationToTest();
        String loadStrategyName = c.getLoadStrategyName();
        assertNotNull(loadStrategyName);

        LoadStrategy s = f.buildInstance(c, new MockLoadConfiguration());
        ServiceType t2 = s.getServiceType();

        assertEquals(t, t2);

        //
        // make sure the name of the strategy is configured
        //
        assertEquals(loadStrategyName, s.getName());
    }

    @Test
    public void buildInstance_nullOrMissingLoadStrategyName() throws Exception {

        LoadStrategyFactory f = getLoadStrategyFactoryToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        ServiceConfiguration sc = getCorrespondingConfigurationToTest();
        MockServiceConfiguration msc = (MockServiceConfiguration)sc;
        msc.setLoadStrategyName(null);

        try {

            f.buildInstance(sc, mlc);
            fail("should have failed");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("missing or null '" + LoadStrategy.NAME_LABEL + "' map element", msg);
        }
    }

    @Test
    public void buildInstance_unknownLoadStrategy() throws Exception {

        LoadStrategyFactory f = getLoadStrategyFactoryToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        ServiceConfiguration sc = getCorrespondingConfigurationToTest();
        MockServiceConfiguration msc = (MockServiceConfiguration)sc;

        msc.setLoadStrategyName("surely-there-is-no-such-load-strategy");

        try {

            f.buildInstance(msc, mlc);
            fail("should have failed");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("ClassNotFoundException io.novaordis.gld.api." + f.getServiceType() +
                    ".load.SurelyThereIsNoSuchLoadStrategy", msg);
        }
    }

    @Test
    public void buildInstance_DoesNotRequireConfig() throws Exception {

        //
        // we simulate a load strategy that does not require any special configuration
        //

        LoadStrategyFactory f = getLoadStrategyFactoryToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockServiceConfiguration  msc = (MockServiceConfiguration)getCorrespondingConfigurationToTest();

        msc.setLoadStrategyName("mock");

        LoadStrategy s = f.buildInstance(msc, mlc);

        //
        // make sure the load strategy was built without any incident
        //

        assertNotNull(s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract LoadStrategyFactory getLoadStrategyFactoryToTest() throws Exception;
    protected abstract ServiceConfiguration getCorrespondingConfigurationToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

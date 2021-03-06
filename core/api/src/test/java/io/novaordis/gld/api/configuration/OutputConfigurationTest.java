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

import io.novaordis.gld.api.sampler.SamplerConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public abstract class OutputConfigurationTest extends LowLevelConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(OutputConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void getSamplerConfiguration_Null() throws Exception {

        Map<String, Object> raw = new HashMap<>();
        raw.put(OutputConfiguration.SAMPLER_CONFIGURATION_LABEL, new HashMap<>());
        //noinspection unchecked
        ((Map<String, Object>)raw.get(OutputConfiguration.SAMPLER_CONFIGURATION_LABEL)).put("something", "somethingelse");
        OutputConfiguration oc = getConfigurationToTest(raw, new File("."));

        SamplerConfiguration sc = oc.getSamplerConfiguration();

        assertNotNull(sc);

        log.debug(".");
    }

    @Test
    public void getSamplerConfiguration_NotNull() throws Exception {

        Map<String, Object> raw = new HashMap<>();
        OutputConfiguration oc = getConfigurationToTest(raw, new File("."));

        SamplerConfiguration sc = oc.getSamplerConfiguration();

        assertNull(sc);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected abstract OutputConfiguration getConfigurationToTest(
            Map<String, Object> rawMap, File configurationDirectory) throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

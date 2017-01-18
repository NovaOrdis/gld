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

package io.novaordis.gld.api.sampler;

import io.novaordis.gld.api.configuration.LowLevelConfigurationBase;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/12/17
 */
public class MockSamplerConfiguration extends LowLevelConfigurationBase implements SamplerConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockSamplerConfiguration() {

        this(new HashMap<>());
    }

    public MockSamplerConfiguration(Map<String, Object> raw) {
        super(raw, new File("."));
    }

    // SamplerConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public String getFormat() {
        throw new NotYetImplementedException("getFormat() NOT YET IMPLEMENTED");
    }

    @Override
    public File getFile() throws UserErrorException {
        throw new NotYetImplementedException("getFile() NOT YET IMPLEMENTED");
    }

    @Override
    public int getSamplingInterval() {
        throw new NotYetImplementedException("getSamplingInterval() NOT YET IMPLEMENTED");
    }

    @Override
    public int getSamplingTaskRunInterval() {
        throw new NotYetImplementedException("getSamplingTaskRunInterval() NOT YET IMPLEMENTED");
    }

    @Override
    public List<String> getMetrics() {
        throw new NotYetImplementedException("getMetrics() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

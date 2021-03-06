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

package io.novaordis.gld.extensions.jboss.eap.jms;

import io.novaordis.gld.api.jms.MockJmsServiceConfiguration;
import io.novaordis.gld.api.jms.embedded.EmbeddedConnectionFactory;
import io.novaordis.gld.api.jms.load.SendLoadStrategy;
import io.novaordis.gld.api.service.ServiceFactory;
import io.novaordis.utilities.UserErrorException;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public class JBossEap7JmsServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossEap7JmsServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void cleanUp() throws Exception {

        MockInitialContextFactory.reset();
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    // version ---------------------------------------------------------------------------------------------------------

    @Test
    public void version() throws Exception {

        JBossEap7JmsService s = new JBossEap7JmsService();

        String version = s.getVersion();

        log.info(version);

        assertNotNull(version);

        String mavenInjectedProjectVersion = System.getProperty("maven.injected.project.version");
        assertNotNull(mavenInjectedProjectVersion);
        assertEquals(mavenInjectedProjectVersion, version);
    }

    // extensionNameToExtensionServiceFullyQualifiedClassName() --------------------------------------------------------

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName() throws Exception {

        String extensionName = "jboss-eap-7-jms";
        String className = ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName(extensionName);

        assertEquals("io.novaordis.gld.extensions.jboss.eap.jms.JBossEap7JmsService", className);
    }

    // configure() -----------------------------------------------------------------------------------------------------

    @Test
    public void configure_JndiUrlIsMissing() throws Exception {

        JBossEap7JmsService s = new JBossEap7JmsService();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration(new HashMap<>(), new File("."));

        try {

            s.configure(msc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.matches("missing required 'jndi-url' configuration element"));
        }
    }

    // start() ---------------------------------------------------------------------------------------------------------

    @Test
    public void start_JndiUrlNotInitialized() throws Exception {

        JBossEap7JmsService s = new JBossEap7JmsService();

        assertNull(s.getJndiUrl());

        try {

            s.start();

            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("JNDI"));
            assertTrue(msg.contains("not initialized"));
        }
    }

    @Test
    public void start_NobodyListensAtJndiUrl() throws Exception {

        JBossEap7JmsService s = new JBossEap7JmsService();

        s.setJndiUrl("mock://invalid-server");
        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        MockInitialContextFactory.setValidJndiUrl("mock://valid-server");

        try {

            s.start();

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("mock://invalid-server"));

            Throwable cause = e.getCause();
            assertTrue(cause instanceof NamingException);
        }
    }

    @Test
    public void start_NoSuchConnectionFactory() throws Exception {

        JBossEap7JmsService s = new JBossEap7JmsService();

        s.setLoadStrategy(new SendLoadStrategy());

        String validJndiUrl = "mock://valid-server";
        s.setJndiUrl(validJndiUrl);
        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        MockInitialContextFactory.setValidJndiUrl(validJndiUrl);

        s.setConnectionFactoryName("/something");

        try {

            s.start();

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("connection factory /something not bound in JNDI"));
        }
    }

    @Test
    public void lifecycle() throws Exception {

        JBossEap7JmsService s = new JBossEap7JmsService();

        s.setLoadStrategy(new SendLoadStrategy());

        String validJndiUrl = "mock://valid-server";
        s.setJndiUrl(validJndiUrl);
        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        MockInitialContextFactory.setValidJndiUrl(validJndiUrl);

        s.setConnectionFactoryName("/MockConnectionFactory");
        MockInitialContextFactory.bind("/MockConnectionFactory", new EmbeddedConnectionFactory());

        s.start();

        assertTrue(s.isStarted());

        s.stop();

        assertFalse(s.isStarted());
    }

    // resolveConnectionFactory() --------------------------------------------------------------------------------------

    @Test
    public void resolveConnectionFactory_NullConnectionFactoryName() throws Exception {

        JBossEap7JmsService s = new JBossEap7JmsService();

        try {

            //noinspection ConstantConditions
            s.resolveConnectionFactory(null);

            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null connection factory name"));
        }
    }

    // resolveDestination() --------------------------------------------------------------------------------------------

    @Test
    public void resolveConnectionFactory_NullDestination() throws Exception {

        JBossEap7JmsService s = new JBossEap7JmsService();

        try {

            //noinspection ConstantConditions
            s.resolveDestination(null);

            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null destination"));
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

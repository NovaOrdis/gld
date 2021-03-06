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

package com.novaordis.gld.strategy.load.cache.http.operations;

import com.novaordis.gld.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.Service;
import com.novaordis.gld.service.cache.infinispan.InfinispanService;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import org.infinispan.client.hotrod.RemoteCache;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public abstract class HttpSessionOperation implements Operation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private HttpSessionSimulation httpSession;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected HttpSessionOperation(HttpSessionSimulation httpSession) {
        this.httpSession = httpSession;
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    @Override
    public LoadStrategy getLoadStrategy() {
        throw new RuntimeException("getLoadStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public void perform(Service s) throws Exception {

        if (!(s instanceof InfinispanService)) {
            throw new IllegalArgumentException("invalid service type " + s + ", we expect an InfinispanService");
        }

        InfinispanService is = (InfinispanService)s;

        //noinspection unchecked
        RemoteCache<String, Object> cache = (RemoteCache<String, Object>)is.getCache();

        performInternal(cache);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getSessionId() {

        if (httpSession == null) {
            return null;
        }

        return httpSession.getSessionId();
    }

    public HttpSessionSimulation getHttpSession() {
        return httpSession;
    }

    @Override
    public String toString() {
        return getSessionId();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract void performInternal(RemoteCache<String, Object> cache) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

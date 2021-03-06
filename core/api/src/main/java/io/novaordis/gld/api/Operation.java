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

import io.novaordis.gld.api.service.Service;

/**
 * A generic operation that will be executed against the target service.
 *
 * For example, if the target service is a web server, an associated operation may be a GET or a POST request. If the
 * target service is a cache, an operation may be a key/value pair put.
 */
public interface Operation {

    /**
     * The key that uniquely identifies an operation instance. For a HTTP request, each request may bear an unique
     * request ID, stored as a HTTP request header. For a cache operation, the key is the cache key. For a JMS message,
     * the key is the message ID.
     */
    String getKey();

    /**
     * We must strictly limit the implementation of this method to invocations whose duration we measure. Everything
     * else, especially time consuming tasks that are irrelevant to what we measure, must be performed outside this
     * method.
     *
     * @exception IllegalArgumentException if we next an invalid or inappropriate service instance.
     */
    void perform(Service s) throws Exception;

    /**
     * @return true if this specific operation instance was performed against the target service, false otherwise.
     */
    boolean wasPerformed();

    /**
     * @return true if this specific operation instance was performed against the target service and it was successful,
     * meaning it completed without errors. False otherwise. Note that if an operation is not "successful", it does not
     * necessarily mean it failed; it is possible that it was not performed yet.
     */
    boolean wasSuccessful();

    LoadStrategy getLoadStrategy();

}

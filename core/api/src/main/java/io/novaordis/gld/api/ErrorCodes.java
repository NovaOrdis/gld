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

package io.novaordis.gld.api;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/27/17
 */
public enum ErrorCodes {

    // Constants -------------------------------------------------------------------------------------------------------

    GLD_10001,
    ;

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        String s = super.toString();
        int i = s.indexOf('_');
        return s.substring(0, i) + "-" + s.substring(i + 1);
    }

}

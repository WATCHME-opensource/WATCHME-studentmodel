/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 4/8/2015
 * Copyright: Copyright (C) 2014-2017 WATCHME Consortium
 * License: The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.watchme.modules.commons;


import eu.watchme.modules.commons.constants.ModelId;

import java.util.Properties;

public class DomainCredentials {
    private static String mWatchmeExampleUsername;
    private static String mWatchmeExamplePassword;

    public static void LoadCredentialsSettings(Properties properties) {
        mWatchmeExsampleUsername = properties.getProperty("epass.privacymanager.username.watchme-example");
        mWatchmeExamplePassword = properties.getProperty("epass.privacymanager.secret.watchme-example");
    }

    static String getUsernameForModelId(String modelId) {
        String username = null;
        switch (modelId.toLowerCase()) {
            case ModelId.EXAMPLE:
                username = mWatchmeExampleUsername;
                break;
        }
        return username;
    }

    static String getPasswordForModelId(String modelId) {
        String password = null;
        switch (modelId.toLowerCase()) {
            case ModelId.EXAMPLE:
                password = mWatchmeExamplePassword;
                break;
        }

        return password;
    }
}

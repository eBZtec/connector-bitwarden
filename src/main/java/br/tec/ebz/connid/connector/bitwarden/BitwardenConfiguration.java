/*
 * Copyright (c) 2010-2014 Evolveum
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

package br.tec.ebz.connid.connector.bitwarden;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.spi.ConfigurationProperty;

import java.net.URI;
import java.net.URL;

public class BitwardenConfiguration extends AbstractConfiguration {

    private static final Log LOG = Log.getLog(BitwardenConfiguration.class);

    private String hostUrl;
    private String clientId;
    private GuardedString clientSecret;

    @ConfigurationProperty(
            displayMessageKey = "bitwarden.config.hostUrl" ,
            helpMessageKey = "bitwarden.config.hostUrl.help",
            required = true,
            order = 1
    )
    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    @ConfigurationProperty(
            displayMessageKey = "bitwarden.config.clientId" ,
            helpMessageKey = "bitwarden.config.clientId.help",
            required = true,
            order = 2
    )
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @ConfigurationProperty(
            displayMessageKey = "bitwarden.config.clientSecret" ,
            helpMessageKey = "bitwarden.config.clientSecret.help",
            required = true,
            order = 3,
            confidential = true
    )
    public GuardedString getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(GuardedString clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public void validate() {
        String errorMessage = null;

        if (hostUrl == null || hostUrl.isBlank()) {
            errorMessage = "Host url is required.";
        } else if (clientId == null || clientId.isBlank()) {
            errorMessage = "Client ID is required.";
        } else if (clientSecret == null) {
            errorMessage = "Client secret is required.";
        } else {
            try {
                new URL(hostUrl).toURI();
            } catch (Exception e) {
                errorMessage = "Invalid URL. Please, provide a valid URL.";
            }
        }

        if (errorMessage != null) {
            throw new ConfigurationException(errorMessage);
        }
    }
}
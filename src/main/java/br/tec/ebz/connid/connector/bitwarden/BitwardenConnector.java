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

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.TestOp;

import java.net.MalformedURLException;

@ConnectorClass(displayNameKey = "bitwarden.connector.display", configurationClass = BitwardenConfiguration.class)
public class BitwardenConnector implements Connector, TestOp {

    private static final Log LOG = Log.getLog(BitwardenConnector.class);

    private BitwardenConfiguration configuration;
    private BitwardenConnection connection;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = (BitwardenConfiguration)configuration;
        try {
            this.connection = new BitwardenConnection(this.configuration);

            LOG.ok("Connector instance initialized successfully.");
        } catch (MalformedURLException e) {
            throw new ConnectorException(e);
        }
    }

    @Override
    public void dispose() {
        configuration = null;
        if (connection != null) {
            connection.dispose();
            connection = null;
        }

        LOG.ok("Connector instance disposed successfully.");
    }

    @Override
    public void test() {

    }
}

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

import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.TestOp;

import java.net.MalformedURLException;
import java.util.Set;

@ConnectorClass(displayNameKey = "bitwarden.connector.display", configurationClass = BitwardenConfiguration.class)
public class BitwardenConnector implements Connector, TestOp, CreateOp {

    private static final Log LOG = Log.getLog(BitwardenConnector.class);

    private BitwardenConfiguration configuration;
    private BitwardenConnection connection;

    private MemberProcessing memberProcessing;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = (BitwardenConfiguration)configuration;
        try {
            this.connection = new BitwardenConnection(this.configuration);
            this.connection.setupServices();

            memberProcessing = new MemberProcessing(this.connection.getMembersService());

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

    @Override
    public Uid create(ObjectClass objectClass, Set<Attribute> createAttributes, OperationOptions options) {
        Uid uid = null;
        try {
            if (objectClass.is(MemberProcessing.OBJECT_CLASS_NAME)) {
                uid = memberProcessing.create(createAttributes, options);
            }
        } catch (Exception e) {
            LOG.error("Could not create object, reason: {0}", e.getMessage());
            throw e;
        }

        return uid;
    }
}

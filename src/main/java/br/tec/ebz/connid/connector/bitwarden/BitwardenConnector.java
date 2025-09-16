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

import br.tec.ebz.connid.connector.bitwarden.processing.AccessProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.CollectionsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.AuthenticationException;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Set;

@ConnectorClass(displayNameKey = "bitwarden.connector.display", configurationClass = BitwardenConfiguration.class)
public class BitwardenConnector implements Connector, TestOp, CreateOp, DeleteOp, UpdateDeltaOp, SearchOp<Filter>, SchemaOp {

    private static final Log LOG = Log.getLog(BitwardenConnector.class);

    private BitwardenConfiguration configuration;
    private BitwardenConnection connection;

    private MemberProcessing memberProcessing;
    private GroupsProcessing groupsProcessing;
    private CollectionsProcessing collectionsProcessing;

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
            groupsProcessing = new GroupsProcessing(this.connection.getGroupsService(), this.connection.getMembersService());
            collectionsProcessing = new CollectionsProcessing(this.connection.getCollectionsService());

            LOG.ok("Connector instance initialized successfully.");
        } catch (Exception e) {
            handleConnectorException(e, e.getMessage());
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
            } else if (objectClass.is(GroupsProcessing.OBJECT_CLASS_NAME)) {
                uid = groupsProcessing.create(createAttributes, options);
            } else {
                throw new UnsupportedOperationException("Object " + objectClass.getObjectClassValue() + " is not supported by the connector.");
            }
        } catch (Exception e) {
            LOG.error("Could not create object, reason: {0}", e.getMessage());
            handleConnectorException(e, "Could not create object, reason: " + e.getMessage());
        }

        return uid;
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions options) {
        try {
            if (objectClass.is(MemberProcessing.OBJECT_CLASS_NAME)) {
                memberProcessing.delete(uid, options);
            } else if (objectClass.is(GroupsProcessing.OBJECT_CLASS_NAME)) {
                groupsProcessing.delete(uid, options);
            } else {
                throw new UnsupportedOperationException("Object " + objectClass.getObjectClassValue() + " is not supported by the connector.");
            }
        } catch (Exception e) {
            LOG.error("Could not delete object, reason: {0}", e.getMessage());
            handleConnectorException(e, "Could not delete object, reason: " + e.getMessage());
        }
    }

    @Override
    public Set<AttributeDelta> updateDelta(ObjectClass objectClass, Uid uid, Set<AttributeDelta> modifications, OperationOptions options) {
        try {
            if (objectClass.is(MemberProcessing.OBJECT_CLASS_NAME)) {
                memberProcessing.update(uid, modifications, options);
            } else if (objectClass.is(GroupsProcessing.OBJECT_CLASS_NAME)) {
                groupsProcessing.update(uid, modifications, options);
            } else if (objectClass.is(CollectionsProcessing.OBJECT_CLASS_NAME)) {
                collectionsProcessing.update(uid, modifications, options);
            } else {
                throw new UnsupportedOperationException("Object " + objectClass.getObjectClassValue() + " is not supported by the connector.");
            }
        } catch (Exception e) {
            LOG.error("Could not update object, reason: {0}", e.getMessage());
            handleConnectorException(e, "Could not update object, reason: " + e.getMessage());
        }
        return Set.of();
    }

    @Override
    public FilterTranslator<Filter> createFilterTranslator(ObjectClass objectClass, OperationOptions options) {
        return new FilterTranslator<Filter>() {
            @Override
            public List<Filter> translate(Filter filter) {
                return CollectionUtil.newList(filter);
            }
        };
    }

    @Override
    public void executeQuery(ObjectClass objectClass, Filter query, ResultsHandler handler, OperationOptions options) {
        try {
            if (objectClass.is(MemberProcessing.OBJECT_CLASS_NAME)) {
                memberProcessing.search(query, handler, options);
            } else if (objectClass.is(GroupsProcessing.OBJECT_CLASS_NAME)) {
                groupsProcessing.search(query, handler, options);
            } else if (objectClass.is(CollectionsProcessing.OBJECT_CLASS_NAME)) {
                collectionsProcessing.search(query, handler, options);
            } else {
                throw new UnsupportedOperationException("Object " + objectClass.getObjectClassValue() + " is not supported by the connector.");
            }
        } catch (Exception e) {
            LOG.error("Could not search object, reason: {0}", e.getMessage());
            handleConnectorException(e, "Could not search object, reason: " + e.getMessage());
        }
    }

    @Override
    public Schema schema() {
        SchemaBuilder schemaBuilder = new SchemaBuilder(BitwardenConnector.class);

        schemaBuilder.defineObjectClass(memberProcessing.schema());
        schemaBuilder.defineObjectClass(AccessProcessing.schema(GroupsProcessing.ACCESS_CLASS_NAME));
        schemaBuilder.defineObjectClass(AccessProcessing.schema(CollectionsProcessing.ACCESS_CLASS_NAME));
        schemaBuilder.defineObjectClass(groupsProcessing.schema());
        schemaBuilder.defineObjectClass(collectionsProcessing.schema());

        return schemaBuilder.build();
    }

    private static void handleConnectorException(Exception e, String message) {
        if (e instanceof UnsupportedOperationException) {
            throw new UnsupportedOperationException(message);
        }

        if (e instanceof Fault || e instanceof ProcessingException) {
            throw new ConnectionFailedException(message + ", reason: " + e.getMessage(), e);
        }

        if (e instanceof UnknownUidException || e instanceof NotFoundException) {
            throw new UnknownUidException(e.getMessage() + ", " + message);
        }

        if (e instanceof BadRequestException ex) {
            throw new InvalidAttributeValueException(message + ". Status code response: " + ex.getResponse().getStatus() + ", response: " + ex.getMessage());
        }

        if (e instanceof MalformedURLException) {
            throw new ConfigurationException(message + ", " + e.getMessage());
        }

        if (e instanceof ConnectException) {
            throw new ConnectorException(message);
        }

        if (e instanceof IOException) {
            if ((e instanceof SocketTimeoutException || e instanceof NoRouteToHostException)) {
                throw new OperationTimeoutException(message + ", timeout occured, reason: " + e.getMessage(), e);
            }

            throw new ConnectorIOException(message + " IO exception occcured, reason: " + e.getMessage(), e);
        }

        if (e instanceof NotAuthorizedException ex) {
            throw new InvalidCredentialException(message + ". Cause: " + ex.getMessage());
        }

    }
}

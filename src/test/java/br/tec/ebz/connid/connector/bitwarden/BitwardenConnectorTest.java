package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import jakarta.ws.rs.BadRequestException;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.*;
import org.identityconnectors.framework.common.objects.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BitwardenConnectorTest extends BitwardenConfigurationHandler{

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = BitwardenConnector.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void should_create_connector_instance() {
        BitwardenConfiguration configuration = configFromEnv();

        BitwardenConnector connector = new BitwardenConnector();
        connector.init(configuration);

        assertNotNull(connector.getConfiguration());
        connector.dispose();
    }

    @Test
    void should_thrown_exception_due_to_invalid_authentication_data() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();
        configuration.setAuthUrl("https://identity.wrong.com");
        configuration.setHostUrl("https://api.wrong.com");
        configuration.setClientId("organization.wrongclientid");
        configuration.setClientSecret(new GuardedString("am I a credential?".toCharArray()));

        ConnectorFacade facade = getTestConnection(configuration);
        assertThrows(ConnectionFailedException.class, facade::test);
    }

    @Test
    void should_return_connection_failed_error_due_to_invalid_auth_url_for_test_connection() {
        BitwardenConfiguration configuration = configFromEnv();
        configuration.setAuthUrl("wrong_auth_url");

        ConnectorFacade facade = getTestConnection(configuration);
        assertThrows(ConnectionFailedException.class, facade::test);
    }

    @Test
    void should_return_connection_failed_error_due_to_invalid_auth_url_for_init() {
        BitwardenConfiguration configuration = configFromEnv();
        configuration.setAuthUrl("wrong_auth_url");

        BitwardenConnector connector = new BitwardenConnector();
        assertThrows(ConnectionFailedException.class, () -> connector.init(configuration));
    }

    @Test
    void should_return_connection_failed_error_due_to_wrong_client_secret() {
        BitwardenConfiguration configuration = configFromEnv();
        configuration.setClientSecret(new GuardedString("wrong-password".toCharArray()));

        BitwardenConnector connector = new BitwardenConnector();
        assertThrows(ConnectionFailedException.class, () -> connector.init(configuration));
    }

    @Test
    void should_return_unsupported_exception_due_to_object_class_not_supported() {
        BitwardenConfiguration configuration = configFromEnv();
        ObjectClass unsupportedObjectClass = new ObjectClass("unsupported");

        ConnectorFacade facade = getTestConnection(configuration);

        assertThrows(UnsupportedOperationException.class, () -> facade.create(unsupportedObjectClass, new HashSet<>(), null));
        assertThrows(UnsupportedOperationException.class, () -> facade.delete(unsupportedObjectClass, new Uid("test"), null));
        assertThrows(UnsupportedOperationException.class, () -> facade.updateDelta(unsupportedObjectClass, new Uid("test"), new HashSet<>(), null));
        assertThrows(UnsupportedOperationException.class, () -> facade.search(unsupportedObjectClass, null, new ListResultHandler(), null));
    }

    @Test
    void should_call_handle_connector_exception_when_create_member_fails() throws Exception {
        BitwardenConnector connector = new BitwardenConnector();

        MemberProcessing memberProcessing = mock(MemberProcessing.class);
        GroupsProcessing groupsProcessing = mock(GroupsProcessing.class);

        setField(connector, "memberProcessing", memberProcessing);
        setField(connector, "groupsProcessing", groupsProcessing);

        when(memberProcessing.create(anySet(), any(OperationOptions.class)))
                .thenThrow(new BadRequestException("Invalid values"));

        ObjectClass objectClass = new ObjectClass(MemberProcessing.OBJECT_CLASS_NAME);
        Set<Attribute> attrs = Collections.emptySet();
        OperationOptions options = new OperationOptionsBuilder().build();

        assertThrows(InvalidAttributeValueException.class,
                () -> connector.create(objectClass, attrs, options));
    }

}
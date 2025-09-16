package br.tec.ebz.connid.connector.bitwarden;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenConnectorTest extends BitwardenConfigurationHandler{

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

}
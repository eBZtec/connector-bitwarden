package br.tec.ebz.connid.connector.bitwarden;

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

}
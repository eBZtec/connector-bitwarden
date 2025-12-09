package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.services.MembersService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenConnectionIT extends BitwardenConfigurationHandler {

    @Test
    void should_create_connection() {
        BitwardenConnection connection = new BitwardenConnection(configFromEnv());

        connection.setupServices();

        MembersService membersService = connection.getMembersService();
        assertNotNull(membersService, "Value cannot be null");

        connection.dispose();
    }

}
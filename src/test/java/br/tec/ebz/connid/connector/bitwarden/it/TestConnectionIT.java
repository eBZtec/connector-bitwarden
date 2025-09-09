package br.tec.ebz.connid.connector.bitwarden.it;

import br.tec.ebz.connid.connector.bitwarden.BitwardenConfigurationHandler;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.junit.jupiter.api.Test;

public class TestConnectionIT extends BitwardenConfigurationHandler {

    @Test
    void should_connect_to_bitwarden_server() {
        ConnectorFacade facade = getTestConnection();
        facade.test();
    }
}

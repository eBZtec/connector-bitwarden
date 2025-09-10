package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SearchMemberTest extends BitwardenConfigurationHandler{

    @Test
    void should_list_all_members() {
        ConnectorFacade facade = getTestConnection();

        ListResultHandler handler = new ListResultHandler();

        facade.search(MemberProcessing.OBJECT_CLASS, null, handler, null);

        assertTrue(handler.getObjects().size() > 1);
    }
}

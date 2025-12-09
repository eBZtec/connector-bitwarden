package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.BitwardenConfigurationHandler;
import br.tec.ebz.connid.connector.bitwarden.ListResultHandler;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionsProcessingIT extends BitwardenConfigurationHandler {
    private static final String COLLECTION_ID = "1858ba2d-e5eb-493b-86e5-b345012d9c93";

    @Test
    void should_search_collection_by_equals_filter() {
        ConnectorFacade facade = getTestConnection();

        ListResultHandler handler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(Uid.NAME, COLLECTION_ID);
        EqualsFilter filter = new EqualsFilter(attribute);

        facade.search(CollectionsProcessing.OBJECT_CLASS, filter, handler, null);

        assertEquals(1, handler.getObjects().size());
    }

    @Test
    void should_list_all_collections() {
        ConnectorFacade facade = getTestConnection();
        ListResultHandler handler = new ListResultHandler();

        facade.search(CollectionsProcessing.OBJECT_CLASS, null, handler, null);
        assertTrue(handler.getObjects().size() > 1);
    }

}
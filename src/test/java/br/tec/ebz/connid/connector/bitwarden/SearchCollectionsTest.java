package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.CollectionsProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AndFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class SearchCollectionsTest extends BitwardenConfigurationHandler{
    private ConnectorFacade facade;
    private ListResultHandler handler;
    private String collectionID;

    @BeforeEach
    public void init() {
        facade = getTestConnection();
        handler = new ListResultHandler();
        collectionID = env("BW_COLLECTION_ID");
        assumeTrue(collectionID != null && !collectionID.isBlank(), "BW_COLLECTION_ID not set");
    }

    @Test
    void should_list_all_collections() {
        facade.search(CollectionsProcessing.OBJECT_CLASS, null, handler, null);
        assertTrue(handler.getObjects().size() > 1);
    }

    @Test
    public void should_found_one_collection() {
        Attribute attribute = AttributeBuilder.build(Uid.NAME, collectionID);
        EqualsFilter filter = new EqualsFilter(attribute);

        facade.search(CollectionsProcessing.OBJECT_CLASS, filter, handler, null);
        assertEquals(1, handler.getObjects().size());
    }

    @Test
    public void should_return_unsupported_exception_for_equals_filter_with_attribute_not_supported() {
        Attribute attribute = AttributeBuilder.build(Name.NAME, "test");
        EqualsFilter filter = new EqualsFilter(attribute);

        assertThrows(UnsupportedOperationException.class, () -> facade.search(CollectionsProcessing.OBJECT_CLASS, filter, handler, null));
    }

    @Test
    public void should_return_unsupported_exception_for_unsupported_filter() {
        Attribute attribute = AttributeBuilder.build(GroupSchemaAttributes.NAME, "test");
        EqualsFilter filter1 = new EqualsFilter(attribute);

        Attribute attribute2 = AttributeBuilder.build(GroupSchemaAttributes.NAME, "test2");
        EqualsFilter filter2 = new EqualsFilter(attribute2);

        AndFilter filter = new AndFilter(filter1, filter2);

        assertThrows(UnsupportedOperationException.class, () -> facade.search(CollectionsProcessing.OBJECT_CLASS, filter, handler, null));
    }

    @Test
    public void should_return_unknown_uid_exception_for_not_found_user() {
        Attribute attribute = AttributeBuilder.build(Uid.NAME, "00000000-0000-0000-0000-000000000000");
        EqualsFilter filter = new EqualsFilter(attribute);

        assertThrows(UnknownUidException.class, () -> facade.search(CollectionsProcessing.OBJECT_CLASS, filter, handler, null));
    }
}

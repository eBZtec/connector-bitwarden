package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.BitwardenConfigurationHandler;
import br.tec.ebz.connid.connector.bitwarden.ListResultHandler;
import br.tec.ebz.connid.connector.bitwarden.schema.CollectionSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CollectionsProcessingTest extends BitwardenConfigurationHandler {
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

    @Test
    void should_update_an_existent_group_from_a_collection() {
        ConnectorFacade facade = getTestConnection();
        Uid uid = new Uid(COLLECTION_ID);

        ConnectorObjectBuilder updatedCollectionAccess = new ConnectorObjectBuilder()
                .setObjectClass(new ObjectClass(AccessProcessing.OBJECT_CLASS_NAME))
                .addAttribute(AttributeBuilder.build(Uid.NAME, "c553777c-74db-4ca6-b8ed-b3550159518c"))
                .addAttribute(AttributeBuilder.build(Name.NAME, "c553777c-74db-4ca6-b8ed-b3550159518c"))
                .addAttribute(AttributeBuilder.build("id", "c553777c-74db-4ca6-b8ed-b3550159518c"))
                .addAttribute(AttributeBuilder.build("readOnly",      false))
                .addAttribute(AttributeBuilder.build("hidePasswords", false))
                .addAttribute(AttributeBuilder.build("manage",        true));

        ConnectorObjectReference ref2 = new ConnectorObjectReference(updatedCollectionAccess.build());

        Set<AttributeDelta> deltaAttributes = new HashSet<AttributeDelta>();
        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(CollectionSchemaAttributes.GROUPS);
        builder.addValueToReplace(ref2);

        deltaAttributes.add(builder.build());

        facade.updateDelta(CollectionsProcessing.OBJECT_CLASS, uid, deltaAttributes, null);
    }

    @Test
    void should_add_a_new_group_into_a_collection() {
        ConnectorFacade facade = getTestConnection();
        Uid uid = new Uid(COLLECTION_ID);

        ConnectorObjectBuilder updatedCollectionAccess = new ConnectorObjectBuilder()
                .setObjectClass(new ObjectClass(AccessProcessing.OBJECT_CLASS_NAME))
                .addAttribute(AttributeBuilder.build(Uid.NAME, "208bcdb6-1746-426d-a777-b345012dbc53"))
                .addAttribute(AttributeBuilder.build(Name.NAME, "208bcdb6-1746-426d-a777-b345012dbc53"))
                .addAttribute(AttributeBuilder.build("id", "208bcdb6-1746-426d-a777-b345012dbc53"))
                .addAttribute(AttributeBuilder.build("readOnly",      true))
                .addAttribute(AttributeBuilder.build("hidePasswords", true))
                .addAttribute(AttributeBuilder.build("manage",        false));

        ConnectorObjectReference ref2 = new ConnectorObjectReference(updatedCollectionAccess.build());

        Set<AttributeDelta> deltaAttributes = new HashSet<AttributeDelta>();
        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(CollectionSchemaAttributes.GROUPS);
        builder.addValueToAdd(ref2);

        deltaAttributes.add(builder.build());

        facade.updateDelta(CollectionsProcessing.OBJECT_CLASS, uid, deltaAttributes, null);
    }

    @Test
    void should_delete_group_into_a_collection() {
        ConnectorFacade facade = getTestConnection();
        Uid uid = new Uid(COLLECTION_ID);

        ConnectorObjectBuilder updatedCollectionAccess = new ConnectorObjectBuilder()
                .setObjectClass(new ObjectClass(AccessProcessing.OBJECT_CLASS_NAME))
                .addAttribute(AttributeBuilder.build(Uid.NAME, "208bcdb6-1746-426d-a777-b345012dbc53"))
                .addAttribute(AttributeBuilder.build(Name.NAME, "208bcdb6-1746-426d-a777-b345012dbc53"))
                .addAttribute(AttributeBuilder.build("id", "208bcdb6-1746-426d-a777-b345012dbc53"))
                .addAttribute(AttributeBuilder.build("readOnly",      true))
                .addAttribute(AttributeBuilder.build("hidePasswords", true))
                .addAttribute(AttributeBuilder.build("manage",        false));

        ConnectorObjectReference ref2 = new ConnectorObjectReference(updatedCollectionAccess.build());

        Set<AttributeDelta> deltaAttributes = new HashSet<AttributeDelta>();
        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(CollectionSchemaAttributes.GROUPS);
        builder.addValueToRemove(ref2);

        deltaAttributes.add(builder.build());

        facade.updateDelta(CollectionsProcessing.OBJECT_CLASS, uid, deltaAttributes, null);
    }

}
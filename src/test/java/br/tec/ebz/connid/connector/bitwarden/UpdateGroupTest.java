package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UpdateGroupTest extends BitwardenConfigurationHandler{

    private String name;
    private static final String TEST_COLLECTION_ID = "1858ba2d-e5eb-493b-86e5-b345012d9c93";

    @BeforeEach
    public void generateId() {
        name = "Test Group Update";
    }


    @Test
    void should_create_a_group_with_a_collection_and_then_update_the_collection() {
        ConnectorFacade facade = getTestConnection();

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, name));
        attributes.add(AttributeBuilder.build(GroupSchemaAttributes.EXTERNAL_ID, name));

        List<String> addCollections = new ArrayList<>();
        addCollections.add("id="+TEST_COLLECTION_ID+";ro=1;hp=1;mg=0");

        Attribute collections = AttributeBuilder.build(GroupSchemaAttributes.COLLECTIONS, addCollections);
        attributes.add(collections);

        Uid uid = facade.create(GroupsProcessing.OBJECT_CLASS, attributes, null);

        assertNotNull(uid, "Group uid cannot be null on creation");

        List<String> updateCollections = new ArrayList<>();
        updateCollections.add("id="+TEST_COLLECTION_ID+";ro=0;hp=1;mg=0");

        Set<AttributeDelta> deltaAttributes = new HashSet<>();

        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(GroupSchemaAttributes.COLLECTIONS);
        builder.addValueToAdd(updateCollections);


        List<String> updateCollections2 = new ArrayList<>();
        updateCollections2.add("id="+TEST_COLLECTION_ID+";ro=1;hp=1;mg=0");

        builder.addValueToRemove(updateCollections2);

        deltaAttributes.add(builder.build());

        facade.updateDelta(GroupsProcessing.OBJECT_CLASS, uid, deltaAttributes, null);

        facade.delete(GroupsProcessing.OBJECT_CLASS, uid, null);

    }

}

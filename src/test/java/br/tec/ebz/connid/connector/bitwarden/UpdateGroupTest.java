package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.CollectionAccessProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.CollectionAccessSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.CollectionSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UpdateGroupTest extends BitwardenConfigurationHandler{

    private String name;

    @BeforeEach
    public void generateId() {
        int randomCode = new Random().nextInt(1000);
        name = "Test Group" + randomCode;
    }


    @Test
    void should_create_a_group_with_a_collection_and_then_update_the_collection() {
        ConnectorFacade facade = getTestConnection();

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, name));
        attributes.add(AttributeBuilder.build(GroupSchemaAttributes.EXTERNAL_ID, name));

        ConnectorObjectBuilder access1 = new ConnectorObjectBuilder()
                .setObjectClass(new ObjectClass(CollectionAccessProcessing.OBJECT_CLASS_NAME))
                .addAttribute(AttributeBuilder.build(Uid.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build(Name.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("id", "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("readOnly",      true))
                .addAttribute(AttributeBuilder.build("hidePasswords", true))
                .addAttribute(AttributeBuilder.build("manage",        false));

        ConnectorObjectReference ref1 = new ConnectorObjectReference(access1.build());

        Attribute collections = AttributeBuilder.build("collections", ref1);
        attributes.add(collections);

        Uid uid = facade.create(GroupsProcessing.OBJECT_CLASS, attributes, null);

        assertNotNull(uid, "Group uid cannot be null on creation");

        ConnectorObjectBuilder updatedCollectionAccess = new ConnectorObjectBuilder()
                .setObjectClass(new ObjectClass(CollectionAccessProcessing.OBJECT_CLASS_NAME))
                .addAttribute(AttributeBuilder.build(Uid.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build(Name.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("id", "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("readOnly",      false))
                .addAttribute(AttributeBuilder.build("hidePasswords", true))
                .addAttribute(AttributeBuilder.build("manage",        false));

        ConnectorObjectReference ref2 = new ConnectorObjectReference(updatedCollectionAccess.build());

        Set<AttributeDelta> deltaAttributes = new HashSet<AttributeDelta>();
        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(GroupSchemaAttributes.COLLECTIONS);
        builder.addValueToAdd(ref2);

        deltaAttributes.add(builder.build());

        facade.updateDelta(GroupsProcessing.OBJECT_CLASS, uid, deltaAttributes, null);

        facade.delete(GroupsProcessing.OBJECT_CLASS, uid, null);

    }

    @Test
    void should_create_a_group_with_a_collection_and_then_delete_the_collection() {
        ConnectorFacade facade = getTestConnection();

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, name));
        attributes.add(AttributeBuilder.build(GroupSchemaAttributes.EXTERNAL_ID, name));

        ConnectorObjectBuilder access1 = new ConnectorObjectBuilder()
                .setObjectClass(new ObjectClass(CollectionAccessProcessing.OBJECT_CLASS_NAME))
                .addAttribute(AttributeBuilder.build(Uid.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build(Name.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("id", "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("readOnly",      true))
                .addAttribute(AttributeBuilder.build("hidePasswords", true))
                .addAttribute(AttributeBuilder.build("manage",        false));

        ConnectorObjectReference ref1 = new ConnectorObjectReference(access1.build());

        Attribute collections = AttributeBuilder.build("collections", ref1);
        attributes.add(collections);

        Uid uid = facade.create(GroupsProcessing.OBJECT_CLASS, attributes, null);

        assertNotNull(uid, "Group uid cannot be null on creation");

        ConnectorObjectBuilder updatedCollectionAccess = new ConnectorObjectBuilder()
                .setObjectClass(new ObjectClass(CollectionAccessProcessing.OBJECT_CLASS_NAME))
                .addAttribute(AttributeBuilder.build(Uid.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build(Name.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("id", "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("readOnly",      false))
                .addAttribute(AttributeBuilder.build("hidePasswords", true))
                .addAttribute(AttributeBuilder.build("manage",        false));

        ConnectorObjectReference ref2 = new ConnectorObjectReference(updatedCollectionAccess.build());

        Set<AttributeDelta> deltaAttributes = new HashSet<AttributeDelta>();
        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(GroupSchemaAttributes.COLLECTIONS);
        builder.addValueToRemove(ref2);

        deltaAttributes.add(builder.build());

        facade.updateDelta(GroupsProcessing.OBJECT_CLASS, uid, deltaAttributes, null);

        facade.delete(GroupsProcessing.OBJECT_CLASS, uid, null);

    }

    @Test
    void should_create_a_group_without_collection_and_then_add_a_new_collection() {
        ConnectorFacade facade = getTestConnection();

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, name));
        attributes.add(AttributeBuilder.build(GroupSchemaAttributes.EXTERNAL_ID, name));

        Uid uid = facade.create(GroupsProcessing.OBJECT_CLASS, attributes, null);

        assertNotNull(uid, "Group uid cannot be null on creation");

        ConnectorObjectBuilder updatedCollectionAccess = new ConnectorObjectBuilder()
                .setObjectClass(new ObjectClass(CollectionAccessProcessing.OBJECT_CLASS_NAME))
                .addAttribute(AttributeBuilder.build(Uid.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build(Name.NAME, "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("id", "1858ba2d-e5eb-493b-86e5-b345012d9c93"))
                .addAttribute(AttributeBuilder.build("readOnly",      false))
                .addAttribute(AttributeBuilder.build("hidePasswords", true))
                .addAttribute(AttributeBuilder.build("manage",        false));

        ConnectorObjectReference ref2 = new ConnectorObjectReference(updatedCollectionAccess.build());

        Set<AttributeDelta> deltaAttributes = new HashSet<AttributeDelta>();
        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(GroupSchemaAttributes.COLLECTIONS);
        builder.addValueToAdd(ref2);

        deltaAttributes.add(builder.build());

        facade.updateDelta(GroupsProcessing.OBJECT_CLASS, uid, deltaAttributes, null);
        facade.delete(GroupsProcessing.OBJECT_CLASS, uid, null);
    }
}

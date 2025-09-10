package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UpdateMemberTest extends BitwardenConfigurationHandler {

    private String email;
    private String name;
    private String login;

    private String group = "c1f70335-efdf-4337-a6f8-b35200e5f299";

    @BeforeEach
    public void generateId() {
        int randomCode = new Random().nextInt(1000);
        name = "Test User" + randomCode;
        login = "test.user" + randomCode;
        email = login + "@example.com";
    }

    @Test
    void should_create_valid_member_and_add_a_group_to_the_member_and_then_delete_the_user() {
        ConnectorFacade facade = getTestConnection();

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, email));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.NAME, name));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TWO_FACTOR_ENABLED, false));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.STATUS, 0));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.RESET_PASSWORD_ENROLLED, false));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.SSO_EXTERNAL_ID, login));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TYPE, 1));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.GROUPS, new ArrayList<>()));

        Uid uid = facade.create(MemberProcessing.OBJECT_CLASS, attributes, null);

        assertNotNull(uid, "Uid cannot be null");

        Set<AttributeDelta> deltaAttributes = new HashSet<AttributeDelta>();

        List<String> newGroup = new ArrayList<>();
        newGroup.add("c1f70335-efdf-4337-a6f8-b35200e5f299");

        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(MemberSchemaAttributes.GROUPS);
        builder.addValueToAdd(newGroup);

        deltaAttributes.add(builder.build());

        facade.updateDelta(MemberProcessing.OBJECT_CLASS, uid, deltaAttributes, null);

        ListResultHandler handler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(Uid.NAME, uid.getUidValue());
        EqualsFilter filter = new EqualsFilter(attribute);

        facade.search(MemberProcessing.OBJECT_CLASS, filter, handler, null);

        List<ConnectorObject> objects = handler.getObjects();

        assertEquals(1, objects.size());

        ConnectorObject connectorObject = objects.get(0);

        Attribute groups = connectorObject.getAttributeByName(MemberSchemaAttributes.GROUPS);
        List<Object> groupValues = groups.getValue();

        assertEquals(1, groupValues.size(), "Must have one group added");

        String groupUpdated = groupValues.get(0).toString();

        assertEquals(group, groupUpdated);

        facade.delete(MemberProcessing.OBJECT_CLASS, uid, null);
    }
}

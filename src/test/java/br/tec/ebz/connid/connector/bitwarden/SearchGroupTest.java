package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.ContainsAllValuesFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SearchGroupTest extends BitwardenConfigurationHandler{

    private String email;
    private String name;
    private String login;

    @BeforeEach
    public void generateId() {
        int randomCode = new Random().nextInt(1000);
        name = "Test Group" + randomCode;
        login = "test.user" + randomCode;
        email = login + "@example.com";
    }

    @Test
    void should_list_all_members() {
        ConnectorFacade facade = getTestConnection();

        ListResultHandler handler = new ListResultHandler();

        facade.search(MemberProcessing.OBJECT_CLASS, null, handler, null);

        assertTrue(handler.getObjects().size() > 1);
    }

    @Test
    void should_create_a_group_create_a_member_with_the_group_and_get_the_member_groups_then_delete_member_and_group() {
        ConnectorFacade facade = getTestConnection();

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, name));
        attributes.add(AttributeBuilder.build(GroupSchemaAttributes.EXTERNAL_ID, name));

        Uid groupUid = facade.create(GroupsProcessing.OBJECT_CLASS, attributes, null);
        assertNotNull(groupUid, "Group uid cannot be null on creation");

        List<String> newGroups = new ArrayList<>();
        newGroups.add(groupUid.getUidValue());

        attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, email));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.NAME, name));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TWO_FACTOR_ENABLED, false));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.STATUS, 0));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.RESET_PASSWORD_ENROLLED, false));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.SSO_EXTERNAL_ID, login));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TYPE, 1));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.GROUPS, newGroups));

        Uid memberUid = facade.create(MemberProcessing.OBJECT_CLASS, attributes, null);

        List<String> members = new ArrayList<>();
        members.add(memberUid.getUidValue());

        assertNotNull(memberUid, "Member uid cannot be null");

        ListResultHandler handler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(GroupSchemaAttributes.MEMBERS, members);
        ContainsAllValuesFilter filter = new ContainsAllValuesFilter(attribute);

        facade.search(GroupsProcessing.OBJECT_CLASS, filter, handler, null);

        List<ConnectorObject> objects = handler.getObjects();

        assertEquals(1, objects.size());
    }
}

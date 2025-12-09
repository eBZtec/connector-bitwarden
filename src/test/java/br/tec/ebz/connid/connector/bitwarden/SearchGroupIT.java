package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenGroup;
import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.repository.ObjectsRepository;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.AndFilter;
import org.identityconnectors.framework.common.objects.filter.ContainsAllValuesFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SearchGroupIT extends BitwardenConfigurationHandler{

    private String email;
    private String groupName;
    private String userName;
    private String login;
    private ConnectorFacade facade;
    private ObjectsRepository objectsRepository;
    private ListResultHandler handler;

    @BeforeEach
    public void generateId() {
        facade = getTestConnection();
        groupName = "Test Group Search";
        userName = "Test User Search";
        login = "test.user.search";
        email = login + "@example.com";

        objectsRepository = new ObjectsRepository(facade);
        handler = new ListResultHandler();
    }

    @Test
    void should_list_all_members() {
        facade.search(GroupsProcessing.OBJECT_CLASS, null, handler, null);
        assertTrue(handler.getObjects().size() > 1);
    }

    @Test
    void should_create_a_group_create_a_member_with_the_group_and_get_the_member_groups_then_delete_member_and_group() {
        ConnectorFacade facade = getTestConnection();

        BitwardenGroup group = new BitwardenGroup();
        group.setName(groupName);
        group.setExternalId(groupName);

        Uid groupUid = objectsRepository.create(group);
        assertNotNull(groupUid, "Group uid cannot be null on creation");

        List<String> newGroups = new ArrayList<>();
        newGroups.add(groupUid.getUidValue());

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, email));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.NAME, userName));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TWO_FACTOR_ENABLED, false));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.STATUS, 0));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.RESET_PASSWORD_ENROLLED, false));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.SSO_EXTERNAL_ID, login));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TYPE, 1));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.GROUPS, newGroups));

        Uid memberUid = facade.create(MemberProcessing.OBJECT_CLASS, attributes, null);

        List<String> members = new ArrayList<>();
        members.add(memberUid.getUidValue());

        Attribute attribute = AttributeBuilder.build(GroupSchemaAttributes.MEMBERS, members);
        ContainsAllValuesFilter filter = new ContainsAllValuesFilter(attribute);

        facade.search(GroupsProcessing.OBJECT_CLASS, filter, handler, null);

        List<ConnectorObject> objects = handler.getObjects();
        assertEquals(1, objects.size());

        objectsRepository.delete(memberUid, MemberProcessing.OBJECT_CLASS);
        objectsRepository.delete(groupUid, GroupsProcessing.OBJECT_CLASS);
    }

    @Test
    public void should_return_unsupported_exception_for_equals_filter_with_attribute_not_supported() {
        Attribute attribute = AttributeBuilder.build(GroupSchemaAttributes.NAME, "test");
        EqualsFilter filter = new EqualsFilter(attribute);

        assertThrows(UnsupportedOperationException.class, () -> facade.search(GroupsProcessing.OBJECT_CLASS, filter, handler, null));
    }

    @Test
    public void should_return_unsupported_exception_for_contains_all_values_filter_with_attribute_not_supported() {
        List<String> values = new ArrayList<>();
        values.add("value1");
        Attribute attribute = AttributeBuilder.build(Uid.NAME, values);
        ContainsAllValuesFilter filter = new ContainsAllValuesFilter(attribute);

        assertThrows(UnsupportedOperationException.class, () -> facade.search(GroupsProcessing.OBJECT_CLASS, filter, handler, null));
    }

    @Test
    public void should_return_unsupported_exception_for_contains_all_values_filter_with_more_than_one_attribute_value() {
        List<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");

        Attribute attribute = AttributeBuilder.build(GroupSchemaAttributes.MEMBERS, values);
        ContainsAllValuesFilter filter = new ContainsAllValuesFilter(attribute);

        assertThrows(UnsupportedOperationException.class, () -> facade.search(GroupsProcessing.OBJECT_CLASS, filter, handler, null));
    }

    @Test
    public void should_return_unsupported_exception_for_unsupported_filter() {
        Attribute attribute = AttributeBuilder.build(GroupSchemaAttributes.NAME, "test");
        EqualsFilter filter1 = new EqualsFilter(attribute);

        Attribute attribute2 = AttributeBuilder.build(GroupSchemaAttributes.NAME, "test2");
        EqualsFilter filter2 = new EqualsFilter(attribute2);

        AndFilter filter = new AndFilter(filter1, filter2);

        assertThrows(UnsupportedOperationException.class, () -> facade.search(GroupsProcessing.OBJECT_CLASS, filter, handler, null));
    }

    @Test
    public void should_return_unknown_uid_exception_for_not_found_group() {
        Attribute attribute = AttributeBuilder.build(Uid.NAME, "00000000-0000-0000-0000-000000000000");
        EqualsFilter filter = new EqualsFilter(attribute);

        assertThrows(UnknownUidException.class, () -> facade.search(GroupsProcessing.OBJECT_CLASS, filter, handler, null));
    }
}

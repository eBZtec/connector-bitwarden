package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenPermissions;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.repository.ObjectsRepository;
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

public class SearchMemberIT extends BitwardenConfigurationHandler{
    private String email;
    private String userName;
    private ConnectorFacade facade;
    private ListResultHandler handler;
    private ObjectsRepository objectsRepository;

    @BeforeEach
    public void generateId() {
        facade = getTestConnection();

        userName = "Test User Member";
        email = "test.user.member@example.com";

        objectsRepository = new ObjectsRepository(facade);
        handler = new ListResultHandler();
    }

    @Test
    void should_list_all_members() {
        facade.search(MemberProcessing.OBJECT_CLASS, null, handler, null);
        assertTrue(handler.getObjects().size() > 1);
    }

    @Test
    public void should_create_member_with_custom_permissions_and_search_by_the_member_created_and_then_delete() {
        BitwardenPermissions permissions = getPermissions();
        BitwardenMember member = new BitwardenMember();
        member.setName(userName);
        member.setStatus(0);
        member.setType(4);
        member.setTwoFactorEnabled(false);
        member.setResetPasswordEnrolled(false);
        member.setPermissions(permissions);
        member.setEmail(email);
        
        Uid uid = objectsRepository.create(member);
        assertNotNull(uid, "Member UID cannot be empty");

        Attribute attribute = AttributeBuilder.build(Uid.NAME, uid.getUidValue());
        EqualsFilter filter = new EqualsFilter(attribute);

        facade.search(MemberProcessing.OBJECT_CLASS, filter, handler, null);

        assertEquals(1, handler.getObjects().size());

        objectsRepository.delete(uid, MemberProcessing.OBJECT_CLASS);
    }

    private BitwardenPermissions getPermissions() {
        BitwardenPermissions permissions = new BitwardenPermissions();
        permissions.setManageUsers(true);
        permissions.setManageScim(false);
        permissions.setManageSso(false);
        permissions.setManagePolicies(false);
        permissions.setManageGroups(false);
        permissions.setManageResetPassword(false);
        permissions.setDeleteAnyCollection(false);
        permissions.setEditAnyCollection(false);
        permissions.setAccessReports(false);
        permissions.setCreateNewCollection(false);

        return permissions;
    }

    @Test
    public void should_return_unsupported_exception_for_equals_filter_with_attribute_not_supported() {
        Attribute attribute = AttributeBuilder.build(Name.NAME, "test");
        EqualsFilter filter = new EqualsFilter(attribute);

        assertThrows(UnsupportedOperationException.class, () -> facade.search(MemberProcessing.OBJECT_CLASS, filter, handler, null));
    }

    @Test
    public void should_return_unsupported_exception_for_unsupported_filter() {
        Attribute attribute = AttributeBuilder.build(GroupSchemaAttributes.NAME, "test");
        EqualsFilter filter1 = new EqualsFilter(attribute);

        Attribute attribute2 = AttributeBuilder.build(GroupSchemaAttributes.NAME, "test2");
        EqualsFilter filter2 = new EqualsFilter(attribute2);

        AndFilter filter = new AndFilter(filter1, filter2);

        assertThrows(UnsupportedOperationException.class, () -> facade.search(MemberProcessing.OBJECT_CLASS, filter, handler, null));
    }

    @Test
    public void should_return_unknown_uid_exception_for_not_found_user() {
        Attribute attribute = AttributeBuilder.build(Uid.NAME, "00000000-0000-0000-0000-000000000000");
        EqualsFilter filter = new EqualsFilter(attribute);

        assertThrows(UnknownUidException.class, () -> facade.search(MemberProcessing.OBJECT_CLASS, filter, handler, null));
    }
}

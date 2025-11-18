package br.tec.ebz.connid.connector.bitwarden.repository;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenGroup;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;

import java.util.HashSet;
import java.util.Set;

public class ObjectsRepository {
    private final ConnectorFacade connectorFacade;

    public ObjectsRepository(ConnectorFacade connectorFacade) {
        this.connectorFacade = connectorFacade;
    }

    public Uid create(BitwardenMember member) {

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, member.getEmail()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.NAME, member.getName()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TWO_FACTOR_ENABLED, member.getTwoFactorEnabled()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.STATUS, member.getStatus()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.RESET_PASSWORD_ENROLLED, member.getResetPasswordEnrolled()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.SSO_EXTERNAL_ID, member.getSsoExternalId()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TYPE, member.getType()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.GROUPS, member.getGroups()));

        if (member.getPermissions() != null) {
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_ACCESS_EVENTS_LOGS, member.getPermissions().getAccessEventsLogs()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_ACCESS_IMPORT_EXPORT, member.getPermissions().getAccessImportExport()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_ACCESS_REPORTS, member.getPermissions().getAccessReports()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_CREATE_NEW_COLLECTIONS, member.getPermissions().getCreateNewCollection()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_EDIT_ANY_COLLECTION, member.getPermissions().getEditAnyCollection()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_DELETE_ANY_COLLECTION, member.getPermissions().getDeleteAnyCollection()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_MANAGE_GROUPS, member.getPermissions().getManageGroups()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_MANAGE_POLICIES, member.getPermissions().getManagePolicies()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_MANAGE_SSO, member.getPermissions().getManageSso()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_MANAGE_USERS, member.getPermissions().getManageUsers()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_MANAGE_RESET_PASSWORD, member.getPermissions().getManageResetPassword()));
            attributes.add(AttributeBuilder.build(MemberSchemaAttributes.PERMISSIONS_MANAGE_SCIM, member.getPermissions().getManageScim()));
        }

        return connectorFacade.create(MemberProcessing.OBJECT_CLASS, attributes, null);
    }

    public Uid create(BitwardenGroup group) {
        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, group.getName()));
        attributes.add(AttributeBuilder.build(GroupSchemaAttributes.EXTERNAL_ID, group.getName()));
        attributes.add(AttributeBuilder.build(GroupSchemaAttributes.COLLECTIONS, group.getCollections()));

        return connectorFacade.create(GroupsProcessing.OBJECT_CLASS, attributes, null);
    }

    public void delete(Uid uid, ObjectClass objectClass) {
        connectorFacade.delete(objectClass, uid, null);
    }
}

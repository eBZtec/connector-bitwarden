package br.tec.ebz.connid.connector.bitwarden.schema;

public interface MemberSchemaAttributes {
    String ID = "id";
    String NAME = "name";
    String EMAIL = "email";
    String TWO_FACTOR_ENABLED = "twoFactorEnabled";
    String STATUS = "status";
    String COLLECTIONS = "collections";
    String RESET_PASSWORD_ENROLLED = "resetPasswordEnrolled";
    String SSO_EXTERNAL_ID = "ssoExternalId";
    String TYPE = "type";
    String EXTERNAL_ID = "externalId";
    String GROUPS = "groups";
    String PERMISSIONS_ACCESS_EVENTS_LOGS = "permissions.accessEventLogs";
    String PERMISSIONS_ACCESS_IMPORT_EXPORT = "permissions.accessImportExport";
    String PERMISSIONS_ACCESS_REPORTS = "permissions.accessReports";
    String PERMISSIONS_CREATE_NEW_COLLECTIONS = "permissions.createNewCollections";
    String PERMISSIONS_EDIT_ANY_COLLECTION = "permissions.editAnyCollection";
    String PERMISSIONS_DELETE_ANY_COLLECTION = "permissions.deleteAnyCollection";
    String PERMISSIONS_MANAGE_GROUPS = "permissions.manageGroups";
    String PERMISSIONS_MANAGE_POLICIES = "permissions.managePolicies";
    String PERMISSIONS_MANAGE_SSO = "permissions.manageSso";
    String PERMISSIONS_MANAGE_USERS = "permissions.manageUsers";
    String PERMISSIONS_MANAGE_RESET_PASSWORD = "permissions.manageResetPassword";
    String PERMISSIONS_MANAGE_SCIM = "permissions.manageScim";
}

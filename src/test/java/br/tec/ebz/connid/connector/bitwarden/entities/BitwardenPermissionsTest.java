package br.tec.ebz.connid.connector.bitwarden.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenPermissionsTest {

    private static BitwardenPermissions full(boolean toggleLast) {
        BitwardenPermissions p = new BitwardenPermissions();
        p.setAccessEventsLogs(true);
        p.setAccessImportExport(true);
        p.setAccessReports(true);
        p.setCreateNewCollection(true);
        p.setEditAnyCollection(true);
        p.setDeleteAnyCollection(true);
        p.setManageGroups(true);
        p.setManagePolicies(true);
        p.setManageSso(true);
        p.setManageUsers(true);
        p.setManageResetPassword(true);
        p.setManageScim(toggleLast);
        return p;
    }

    private static BitwardenPermissions clone(BitwardenPermissions src) {
        BitwardenPermissions p = new BitwardenPermissions();
        p.setAccessEventsLogs(src.getAccessEventsLogs());
        p.setAccessImportExport(src.getAccessImportExport());
        p.setAccessReports(src.getAccessReports());
        p.setCreateNewCollection(src.getCreateNewCollection());
        p.setEditAnyCollection(src.getEditAnyCollection());
        p.setDeleteAnyCollection(src.getDeleteAnyCollection());
        p.setManageGroups(src.getManageGroups());
        p.setManagePolicies(src.getManagePolicies());
        p.setManageSso(src.getManageSso());
        p.setManageUsers(src.getManageUsers());
        p.setManageResetPassword(src.getManageResetPassword());
        p.setManageScim(src.getManageScim());
        return p;
    }

    @Test
    void test_getters_and_setters_basic() {
        BitwardenPermissions p = new BitwardenPermissions();

        p.setAccessEventsLogs(true);
        p.setAccessImportExport(true);
        p.setAccessReports(true);
        p.setCreateNewCollection(true);
        p.setEditAnyCollection(true);
        p.setDeleteAnyCollection(true);
        p.setManageGroups(true);
        p.setManagePolicies(true);
        p.setManageSso(true);
        p.setManageUsers(true);
        p.setManageResetPassword(true);
        p.setManageScim(true);

        assertTrue(p.getAccessEventsLogs());
        assertTrue(p.getAccessImportExport());
        assertTrue(p.getAccessReports());
        assertTrue(p.getCreateNewCollection());
        assertTrue(p.getEditAnyCollection());
        assertTrue(p.getDeleteAnyCollection());
        assertTrue(p.getManageGroups());
        assertTrue(p.getManagePolicies());
        assertTrue(p.getManageSso());
        assertTrue(p.getManageUsers());
        assertTrue(p.getManageResetPassword());
        assertTrue(p.getManageScim());
    }

    @Test
    void test_get_access_import_export_when_access_events_logs_is_null() {
        BitwardenPermissions p = new BitwardenPermissions();

        p.setAccessImportExport(true);

        assertFalse(p.getAccessImportExport());
    }

    @Test
    void test_null_and_ling_getters_return_false() {
        BitwardenPermissions p = new BitwardenPermissions();

        p.setAccessReports(null);
        p.setCreateNewCollection(null);
        p.setDeleteAnyCollection(null);
        p.setManageGroups(null);
        p.setManagePolicies(null);
        p.setManageSso(null);
        p.setManageUsers(null);
        p.setManageResetPassword(null);
        p.setManageScim(null);

        assertFalse(p.getAccessReports());
        assertFalse(p.getCreateNewCollection());
        assertFalse(p.getDeleteAnyCollection());
        assertFalse(p.getManageGroups());
        assertFalse(p.getManagePolicies());
        assertFalse(p.getManageSso());
        assertFalse(p.getManageUsers());
        assertFalse(p.getManageResetPassword());
        assertFalse(p.getManageScim());
    }

    static Stream<Arguments> equalsCases() {

        BitwardenPermissions base = full(true);
        BitwardenPermissions same = full(true);

        BitwardenPermissions diffAccessEventsLogs = clone(base);
        diffAccessEventsLogs.setAccessEventsLogs(false);

        BitwardenPermissions diffAccessImportExport = clone(base);
        diffAccessImportExport.setAccessImportExport(false);

        BitwardenPermissions diffAccessReports = clone(base);
        diffAccessReports.setAccessReports(false);

        BitwardenPermissions diffCreateNewCollection = clone(base);
        diffCreateNewCollection.setCreateNewCollection(false);

        BitwardenPermissions diffEditAnyCollection = clone(base);
        diffEditAnyCollection.setEditAnyCollection(false);

        BitwardenPermissions diffDeleteAnyCollection = clone(base);
        diffDeleteAnyCollection.setDeleteAnyCollection(false);

        BitwardenPermissions diffManageGroups = clone(base);
        diffManageGroups.setManageGroups(false);

        BitwardenPermissions diffManagePolicies = clone(base);
        diffManagePolicies.setManagePolicies(false);

        BitwardenPermissions diffManageSso = clone(base);
        diffManageSso.setManageSso(false);

        BitwardenPermissions diffManageUsers = clone(base);
        diffManageUsers.setManageUsers(false);

        BitwardenPermissions diffManageResetPassword = clone(base);
        diffManageResetPassword.setManageResetPassword(false);

        BitwardenPermissions diffManageScim = clone(base);
        diffManageScim.setManageScim(false);


        return Stream.of(
                Arguments.of(base, same, true),
                Arguments.of(base, diffAccessEventsLogs, false),
                Arguments.of(base, diffAccessImportExport, false),
                Arguments.of(base, diffAccessReports, false),
                Arguments.of(base, diffCreateNewCollection, false),
                Arguments.of(base, diffEditAnyCollection, false),
                Arguments.of(base, diffDeleteAnyCollection, false),
                Arguments.of(base, diffManageGroups, false),
                Arguments.of(base, diffManagePolicies, false),
                Arguments.of(base, diffManageSso, false),
                Arguments.of(base, diffManageUsers, false),
                Arguments.of(base, diffManageResetPassword, false),
                Arguments.of(base, diffManageScim, false),
                Arguments.of(base, null, false),
                Arguments.of(base, "not-permissions", false)
        );
    }


    @ParameterizedTest
    @MethodSource("equalsCases")
    void testEqualsParametrized(BitwardenPermissions left, Object right, boolean expected) {
        assertEquals(expected, left.equals(right));
    }
}


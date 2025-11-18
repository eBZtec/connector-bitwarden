package br.tec.ebz.connid.connector.bitwarden.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenMemberTest {

    private static BitwardenCollection collection(String id) {
        BitwardenCollection c = new BitwardenCollection();
        c.setId(id);
        c.setObject("collection");
        c.setExternalId("EXT-" + id);
        return c;
    }

    private static BitwardenPermissions permissionsAllTrue() {
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
        return p;
    }

    private static BitwardenPermissions permissionsWithDiff() {
        BitwardenPermissions p = permissionsAllTrue();
        p.setManageUsers(false); // só um campo diferente
        return p;
    }

    private static BitwardenMember member(String object,
                                          String id,
                                          String name,
                                          String email,
                                          Boolean twoFactorEnabled,
                                          Integer status,
                                          List<BitwardenCollection> collections,
                                          Boolean resetPasswordEnrolled,
                                          String ssoExternalId,
                                          Integer type,
                                          String externalId,
                                          List<String> groups,
                                          BitwardenPermissions permissions) {

        BitwardenMember m = new BitwardenMember();
        m.setObject(object);
        m.setId(id);
        m.setName(name);
        m.setEmail(email);
        m.setTwoFactorEnabled(twoFactorEnabled);
        m.setStatus(status);
        m.setCollections(collections);
        m.setResetPasswordEnrolled(resetPasswordEnrolled);
        m.setSsoExternalId(ssoExternalId);
        m.setType(type);
        m.setExternalId(externalId);
        m.setGroups(groups);
        m.setPermissions(permissions);
        return m;
    }

    private BitwardenPermissions createPermissions() {
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
        return p;
    }

    @Test
    void test_getters_and_setters() {
        BitwardenMember m = new BitwardenMember();

        m.setObject("member");
        m.setId("M1");
        m.setName("John Doe");
        m.setEmail("john@example.com");
        m.setTwoFactorEnabled(true);
        m.setStatus(1);
        m.setResetPasswordEnrolled(false);
        m.setSsoExternalId("SSO-1");
        m.setType(2);
        m.setExternalId("EXT-USER");
        m.setCollections(List.of(collection("C1"), collection("C2")));
        m.setGroups(List.of("G1", "G2"));
        m.setPermissions(createPermissions());

        assertEquals("member", m.getObject());
        assertEquals("M1", m.getId());
        assertEquals("John Doe", m.getName());
        assertEquals("john@example.com", m.getEmail());
        assertTrue(m.getTwoFactorEnabled());
        assertEquals(1, m.getStatus());
        assertFalse(m.getResetPasswordEnrolled());
        assertEquals("SSO-1", m.getSsoExternalId());
        assertEquals(2, m.getType());
        assertEquals("EXT-USER", m.getExternalId());
        assertEquals(List.of("G1", "G2"), m.getGroups());
        assertNotNull(m.getCollections());
        assertEquals(2, m.getCollections().size());
        assertNotNull(m.getPermissions());
    }

    @Test
    void test_equals_and_hash_code() {
        BitwardenMember m1 = new BitwardenMember();
        BitwardenMember m2 = new BitwardenMember();

        BitwardenPermissions permissions = createPermissions();
        List<BitwardenCollection> collections = List.of(collection("C1"));
        List<String> groups = List.of("G1", "G2");

        m1.setObject("member");
        m1.setId("M1");
        m1.setName("Name");
        m1.setEmail("email@example.com");
        m1.setTwoFactorEnabled(true);
        m1.setStatus(1);
        m1.setCollections(collections);
        m1.setResetPasswordEnrolled(true);
        m1.setSsoExternalId("SSO");
        m1.setType(2);
        m1.setExternalId("EXT");
        m1.setGroups(groups);
        m1.setPermissions(permissions);

        m2.setObject("member");
        m2.setId("M1");
        m2.setName("Name");
        m2.setEmail("email@example.com");
        m2.setTwoFactorEnabled(true);
        m2.setStatus(1);
        m2.setCollections(collections);
        m2.setResetPasswordEnrolled(true);
        m2.setSsoExternalId("SSO");
        m2.setType(2);
        m2.setExternalId("EXT");
        m2.setGroups(groups);
        m2.setPermissions(createPermissions());

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }



    @Test
    void test_to_string() {
        BitwardenMember m = new BitwardenMember();
        m.setObject("member");
        m.setId("M1");
        m.setName("User");
        m.setEmail("user@example.com");
        m.setStatus(1);
        m.setType(2);
        m.setExternalId("EXT");
        m.setGroups(List.of("G1"));
        m.setCollections(List.of(collection("C1")));
        m.setPermissions(createPermissions());

        String result = m.toString();

        assertTrue(result.contains("BitwardenMember{"));
        assertTrue(result.contains("object='member'"));
        assertTrue(result.contains("id='M1'"));
        assertTrue(result.contains("name='User'"));
        assertTrue(result.contains("email='user@example.com'"));
        assertTrue(result.contains("status=1"));
        assertTrue(result.contains("type=2"));
        assertTrue(result.contains("externalId='EXT'"));
        assertTrue(result.contains("groups="));
        assertTrue(result.contains("collections="));
        assertTrue(result.contains("permissions="));
    }

    static Stream<Arguments> equalsCases() {
        BitwardenMember base = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember same = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentObject = member(
                "other-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentId = member(
                "member-object",
                "OTHER_ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentName = member(
                "member-object",
                "ID",
                "Other Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentEmail = member(
                "member-object",
                "ID",
                "User Name",
                "other@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentTwoFactor = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                false,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentStatus = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                2,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentCollections = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C2")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentResetPasswordEnrolled = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                false,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentSsoExternalId = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "OTHER_SSO",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentType = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                3,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentExternalId = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "OTHER_EXT",
                List.of("G1", "G2"),
                permissionsAllTrue()
        );

        BitwardenMember differentGroups = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G3"),
                permissionsAllTrue()
        );

        BitwardenMember differentPermissions = member(
                "member-object",
                "ID",
                "User Name",
                "user@example.com",
                true,
                1,
                List.of(collection("C1")),
                true,
                "SSO-1",
                2,
                "EXT-USER",
                List.of("G1", "G2"),
                permissionsWithDiff()
        );

        return Stream.of(
                Arguments.of(base, same, true),
                Arguments.of(base, differentObject, false),
                Arguments.of(base, differentId, false),
                Arguments.of(base, differentName, false),
                Arguments.of(base, differentEmail, false),
                Arguments.of(base, differentTwoFactor, false),
                Arguments.of(base, differentStatus, false),
                Arguments.of(base, differentCollections, false),
                Arguments.of(base, differentResetPasswordEnrolled, false),
                Arguments.of(base, differentSsoExternalId, false),
                Arguments.of(base, differentType, false),
                Arguments.of(base, differentExternalId, false),
                Arguments.of(base, differentGroups, false),
                Arguments.of(base, differentPermissions, false),

                Arguments.of(base, null, false),
                Arguments.of(base, "not a member", false)
        );
    }

    @ParameterizedTest
    @MethodSource("equalsCases")
    void testEqualsParametrized(BitwardenMember left, Object right, boolean expected) {
        assertEquals(expected, left.equals(right));
    }
}


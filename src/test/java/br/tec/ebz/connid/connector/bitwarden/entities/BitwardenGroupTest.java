package br.tec.ebz.connid.connector.bitwarden.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenGroupTest {

    private static BitwardenAccess access(String id, Boolean ro, Boolean hp, Boolean mg) {
        BitwardenAccess a = new BitwardenAccess();
        a.setId(id);
        a.setReadOnly(ro);
        a.setHidePassword(hp);
        a.setManage(mg);
        return a;
    }

    private static BitwardenGroup group(String id,
                                        String object,
                                        String name,
                                        String externalId,
                                        List<BitwardenAccess> collections,
                                        List<String> members) {

        BitwardenGroup g = new BitwardenGroup();
        g.setId(id);
        g.setObject(object);
        g.setName(name);
        g.setExternalId(externalId);
        g.setCollections(collections);
        g.setMembers(members);
        return g;
    }

    @Test
    void test_getters_and_setters() {
        BitwardenGroup group = new BitwardenGroup();

        group.setId("G1");
        group.setObject("group");
        group.setName("Admins");
        group.setExternalId("EXT-123");

        List<BitwardenAccess> accesses = List.of(
                access("A1", true, true, false),
                access("A2", false, true, true)
        );
        List<String> members = List.of("user1", "user2");

        group.setCollections(accesses);
        group.setMembers(members);

        assertEquals("G1", group.getId());
        assertEquals("group", group.getObject());
        assertEquals("Admins", group.getName());
        assertEquals("EXT-123", group.getExternalId());
        assertEquals(accesses, group.getCollections());
        assertEquals(members, group.getMembers());
    }

    @Test
    void test_equals_and_hash_code() {
        BitwardenGroup g1 = new BitwardenGroup();
        BitwardenGroup g2 = new BitwardenGroup();

        g1.setId("1");
        g1.setObject("group");
        g1.setName("Team");
        g1.setExternalId("EXT");
        g1.setCollections(List.of(access("A", true, false, true)));
        g1.setMembers(List.of("U1", "U2"));

        g2.setId("1");
        g2.setObject("group");
        g2.setName("Team");
        g2.setExternalId("EXT");
        g2.setCollections(List.of(access("A", true, false, true)));
        g2.setMembers(List.of("U1", "U2"));

        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void test_to_string() {
        BitwardenGroup group = new BitwardenGroup();
        group.setId("ID1");
        group.setObject("collection");
        group.setName("Managers");
        group.setExternalId("EXT1");
        group.setCollections(List.of(access("ACC", true, true, true)));
        group.setMembers(List.of("userA"));

        String result = group.toString();

        assertTrue(result.contains("id='ID1'"));
        assertTrue(result.contains("object='collection'"));
        assertTrue(result.contains("name='Managers'"));
        assertTrue(result.contains("externalId='EXT1'"));
        assertTrue(result.contains("collections=["));
        assertTrue(result.contains("members=["));
    }

    static Stream<Arguments> equalsCases() {
        BitwardenGroup base = group(
                "ID",
                "group-object",
                "Admins",
                "EXT",
                List.of(access("A1", false, false, false)),
                List.of("user1", "user2")
        );

        BitwardenGroup same = group(
                "ID",
                "group-object",
                "Admins",
                "EXT",
                List.of(access("A1", false, false, false)),
                List.of("user1", "user2")
        );

        BitwardenGroup differentId = group(
                "OTHER_ID",          // id diferente
                "group-object",
                "Admins",
                "EXT",
                List.of(access("A1", false, false, false)),
                List.of("user1", "user2")
        );

        BitwardenGroup differentObject = group(
                "ID",
                "other-object",      // object diferente
                "Admins",
                "EXT",
                List.of(access("A1", false, false, false)),
                List.of("user1", "user2")
        );

        BitwardenGroup differentName = group(
                "ID",
                "group-object",
                "Users",             // name diferente
                "EXT",
                List.of(access("A1", false, false, false)),
                List.of("user1", "user2")
        );

        BitwardenGroup differentExternalId = group(
                "ID",
                "group-object",
                "Admins",
                "OTHER_EXT",
                List.of(access("A1", false, false, false)),
                List.of("user1", "user2")
        );

        BitwardenGroup differentCollections = group(
                "ID",
                "group-object",
                "Admins",
                "EXT",
                List.of(access("A2", true, false, false)),
                List.of("user1", "user2")
        );

        BitwardenGroup differentMembers = group(
                "ID",
                "group-object",
                "Admins",
                "EXT",
                List.of(access("A1", false, false, false)),
                List.of("user3")
        );

        return Stream.of(
                Arguments.of(base, same, true),
                Arguments.of(base, differentId, false),
                Arguments.of(base, differentObject, false),
                Arguments.of(base, differentName, false),
                Arguments.of(base, differentExternalId, false),
                Arguments.of(base, differentCollections, false),
                Arguments.of(base, differentMembers, false),
                Arguments.of(base, null, false),
                Arguments.of(base, "not a group", false)
        );
    }

    @ParameterizedTest
    @MethodSource("equalsCases")
    void testEqualsParametrized(BitwardenGroup left, Object right, boolean expected) {
        assertEquals(expected, left.equals(right));
    }
}

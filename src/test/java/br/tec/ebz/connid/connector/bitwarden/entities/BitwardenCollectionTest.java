package br.tec.ebz.connid.connector.bitwarden.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenCollectionTest {

    private static BitwardenAccess access(String id, Boolean readOnly, Boolean hidePassword, Boolean manage) {
        BitwardenAccess a = new BitwardenAccess();
        a.setId(id);
        a.setReadOnly(readOnly);
        a.setHidePassword(hidePassword);
        a.setManage(manage);
        return a;
    }

    private static BitwardenCollection collection(String id,
                                                  String externalId,
                                                  List<BitwardenAccess> groups,
                                                  String object) {
        BitwardenCollection c = new BitwardenCollection();
        c.setId(id);
        c.setExternalId(externalId);
        c.setGroups(groups);
        c.setObject(object);
        return c;
    }

    @Test
    void should_test_getters_and_setters() {
        BitwardenCollection c = new BitwardenCollection();

        c.setId("123");
        c.setExternalId("EXT-1");
        c.setObject("collection");
        c.setGroups(List.of(access("A", true, false, true), access("B", false, false, false)));

        assertEquals("123", c.getId());
        assertEquals("EXT-1", c.getExternalId());
        assertEquals("collection", c.getObject());
        assertEquals(List.of(access("A", true, false, true), access("B", false, false, false)), c.getGroups());
    }

    @Test
    void should_test_equals_and_hashCode() {
        BitwardenCollection c1 = new BitwardenCollection();
        BitwardenCollection c2 = new BitwardenCollection();

        c1.setId("C1");
        c1.setExternalId("EXT-1");
        c1.setObject("obj");
        c1.setGroups(List.of(access("G1", false, false, true)));

        c2.setId("C1");
        c2.setExternalId("EXT-1");
        c2.setObject("obj");
        c2.setGroups(List.of(access("G1", false, false, true)));

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void should_test_equals_null_or_different_class() {
        BitwardenCollection c1 = new BitwardenCollection();
        c1.setId("1");
        c1.setObject("obj");
        c1.setGroups(null);
        c1.setExternalId("ext");

        BitwardenCollection c2 = new BitwardenCollection();
        c2.setId("2");
        c2.setObject("obj");
        c2.setGroups(null);
        c2.setExternalId("ext");

        assertNotEquals(true, c1.equals(c2));
    }

    @Test
    void should_test_equals_class() {
        BitwardenCollection c1 = new BitwardenCollection();
        c1.setId("1");
        c1.setObject("obj");
        c1.setGroups(null);
        c1.setExternalId("ext");

        assertNotEquals(true, c1.equals(new Object()));
    }

    @Test
    void should_test_to_string() {
        BitwardenCollection c = new BitwardenCollection();
        c.setId("ID1");
        c.setExternalId("EXT");
        c.setObject("OBJ");
        c.setGroups(List.of(access("A", false, true, true)));

        String s = c.toString();

        assertTrue(s.contains("id='ID1'"));
        assertTrue(s.contains("externalId='EXT'"));
        assertTrue(s.contains("object='OBJ'"));
        assertTrue(s.contains("groups=["));
        assertTrue(s.contains("ro=0"));
        assertTrue(s.contains("hp=1"));
        assertTrue(s.contains("mg=1"));
    }

    static Stream<Arguments> equalsCases() {
        BitwardenCollection base = collection(
                "ID",
                "EXT",
                List.of(access("A1", true, false, true)),
                "object"
        );

        BitwardenCollection same = collection(
                "ID",
                "EXT",
                List.of(access("A1", true, false, true)),
                "object"
        );

        BitwardenCollection differentId = collection(
                "OTHER_ID",
                "EXT",
                List.of(access("A1", true, false, true)),
                "object"
        );

        BitwardenCollection differentExternalId = collection(
                "ID",
                "OTHER_EXT",
                List.of(access("A1", true, false, true)),
                "object"
        );

        BitwardenCollection differentGroups = collection(
                "ID",
                "EXT",
                List.of(access("A2", true, true, false)), // só muda o grupo
                "object"
        );

        BitwardenCollection differentObject = collection(
                "ID",
                "EXT",
                List.of(access("A1", true, false, true)),
                "otherObject"
        );

        return Stream.of(
                Arguments.of(base, same, true),
                Arguments.of(base, differentId, false),
                Arguments.of(base, differentExternalId, false),
                Arguments.of(base, differentGroups, false),
                Arguments.of(base, differentObject, false),
                Arguments.of(base, null, false),
                Arguments.of(base, "not a collection", false)
        );
    }

    @ParameterizedTest
    @MethodSource("equalsCases")
    void test_equals_parametrized(BitwardenCollection left, Object right, boolean expected) {
        assertEquals(expected, left.equals(right));
    }

}

package br.tec.ebz.connid.connector.bitwarden.entities;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenCollectionTest {

    private BitwardenAccess access(String id, Boolean readOnly, Boolean hidePassword, Boolean manage) {
        BitwardenAccess a = new BitwardenAccess();
        a.setId(id);
        a.setReadOnly(readOnly);
        a.setHidePassword(hidePassword);
        a.setManage(manage);
        return a;
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
    void should_test_get_groups_returns_empty_list_when_null() {
        BitwardenCollection c = new BitwardenCollection();

        assertNotNull(c.getGroups());
        assertTrue(c.getGroups().isEmpty());
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
    void should_test_equals_different_objects() {
        BitwardenCollection c1 = new BitwardenCollection();
        BitwardenCollection c2 = new BitwardenCollection();

        c1.setId("1");
        c1.setExternalId("A");
        c1.setObject("obj");
        c1.setGroups(List.of(access("X", false, false, true)));

        c2.setId("2");
        c2.setExternalId("A");
        c2.setObject("obj");
        c2.setGroups(List.of(access("X", false, false, true)));

        assertNotEquals(c1, c2);
    }

    @Test
    void should_test_equals_null_or_different_class() {
        BitwardenCollection c1 = new BitwardenCollection();
        c1.setId("1");
        c1.setObject("obj");

        BitwardenCollection c2 = new BitwardenCollection();
        c2.setId("2");
        c2.setObject("obj");

        assertNotEquals(c1, c2);
    }

    @Test
    void should_test_equals_class() {
        BitwardenCollection c1 = new BitwardenCollection();
        c1.setId("1");
        c1.setObject("obj");

        BitwardenCollection c2 = new BitwardenCollection();
        c2.setId("1");
        c2.setObject("obj");

        assertEquals(c1, c2);
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

}

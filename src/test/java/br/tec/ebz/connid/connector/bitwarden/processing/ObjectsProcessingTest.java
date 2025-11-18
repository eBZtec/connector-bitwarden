package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenAccess;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectsProcessingTest {
    private static Method extractIdLooseMethod;
    private static Method parseIdsMethod;
    private final ObjectProcessingImpl processing = new ObjectProcessingImpl();

    @BeforeAll
    static void setup() throws Exception {
        extractIdLooseMethod =
                ObjectProcessing.class.getDeclaredMethod("extractIdLoose", String.class);
        extractIdLooseMethod.setAccessible(true);

        parseIdsMethod = ObjectProcessing.class.getDeclaredMethod("parseIds", List.class);
        parseIdsMethod.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    private Set<String> callParseIds(List<Object> rawValues) {
        try {
            Object result = parseIdsMethod.invoke(null, rawValues);
            return (Set<String>) result;
        } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getCause());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String callExtractIdLoose(String input) {
        try {
            return (String) extractIdLooseMethod.invoke(null, input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void test_getAttributeValue_should_return_correct_values_by_type() {
        Set<Attribute> attrs = new HashSet<>();

        attrs.add(AttributeBuilder.build("stringAttr", "abc"));
        attrs.add(AttributeBuilder.build("longAttr", 10L));
        attrs.add(AttributeBuilder.build("intAttr", 5));
        attrs.add(AttributeBuilder.build("boolAttr", true));
        attrs.add(AttributeBuilder.build("listAttr", Arrays.asList("a", "b")));
        GuardedString gs = new GuardedString("secret".toCharArray());
        attrs.add(AttributeBuilder.build("guardedAttr", gs));

        String s = ObjectProcessing.getAttributeValue("stringAttr", String.class, attrs);
        Long l = ObjectProcessing.getAttributeValue("longAttr", Long.class, attrs);
        Integer i = ObjectProcessing.getAttributeValue("intAttr", Integer.class, attrs);
        Boolean b = ObjectProcessing.getAttributeValue("boolAttr", Boolean.class, attrs);
        List<?> list = ObjectProcessing.getAttributeValue("listAttr", List.class, attrs);
        GuardedString g = ObjectProcessing.getAttributeValue("guardedAttr", GuardedString.class, attrs);
        String missing = ObjectProcessing.getAttributeValue("missingAttr", String.class, attrs);

        assertEquals("abc", s);
        assertEquals(10L, l);
        assertEquals(5, i);
        assertEquals(Boolean.TRUE, b);
        assertEquals(Arrays.asList("a", "b"), list);
        assertNotNull(g);
        assertNull(missing);
    }

    @Test
    void teste_get_attribute_value_unknown_type_should_thrown_invalid_attribute_exception() {
        Set<Attribute> attrs = Set.of(AttributeBuilder.build("x", "y"));

        assertThrows(InvalidAttributeValueException.class, () ->
                ObjectProcessing.getAttributeValue("x", Double.class, attrs)
        );
    }

    @Test
    void test_update_object_attributes_must_add_new_collection_and_copy_other_values() {
        ConnectorObjectBuilder cob = new ConnectorObjectBuilder();
        cob.setUid(new Uid("uid-123"));
        cob.setName("username123");
        cob.setObjectClass(ObjectClass.GROUP);

        cob.addAttribute(AttributeBuilder.build(GroupSchemaAttributes.EXTERNAL_ID, "ext"));
        cob.addAttribute(AttributeBuilder.build(
                GroupSchemaAttributes.COLLECTIONS,
                Arrays.asList(
                        "id=1;ro=1;hp=0;mg=0",
                        "id=2;ro=0;hp=1;mg=0"
                )
        ));

        ConnectorObject oldObject = cob.build();

        Set<AttributeDelta> deltas = new HashSet<>();

        AttributeDeltaBuilder attr1 = new AttributeDeltaBuilder();
        attr1.setName(GroupSchemaAttributes.COLLECTIONS);
        attr1.addValueToRemove(List.of("id=2;ro=0;hp=1;mg=0"));

        deltas.add(attr1.build());

        AttributeDeltaBuilder attr2 = new AttributeDeltaBuilder();
        attr2.setName(GroupSchemaAttributes.COLLECTIONS);
        attr2.addValueToAdd(List.of("id=3;ro=0;hp=0;mg=1"));

        deltas.add(attr2.build());

        AttributeDeltaBuilder attr3 = new AttributeDeltaBuilder();
        attr3.setName(GroupSchemaAttributes.EXTERNAL_ID);
        attr3.addValueToRemove(List.of("ext"));

        deltas.add(attr3.build());

        Set<Attribute> updated =
                processing.updateObjectAttributes(new Uid("uid-123"), deltas, oldObject);

        String externalId = ObjectProcessing.getAttributeValue(GroupSchemaAttributes.EXTERNAL_ID, String.class, updated);

        List<BitwardenAccess> accesses =
                ObjectProcessing.transform(updated, GroupSchemaAttributes.COLLECTIONS);

        assertEquals(2, accesses.size());

        BitwardenAccess acc1 = accesses.stream()
                .filter(a -> "1".equals(a.getId()))
                .findFirst()
                .orElseThrow();

        BitwardenAccess acc3 = accesses.stream()
                .filter(a -> "3".equals(a.getId()))
                .findFirst()
                .orElseThrow();

        assertTrue(acc1.getReadOnly());
        assertFalse(acc1.getHidePasswords());
        assertFalse(acc1.getManage());

        assertFalse(acc3.getReadOnly());
        assertFalse(acc3.getHidePasswords());
        assertTrue(acc3.getManage());

        assertNull(externalId);
    }

    @Test
    void test_update_object_attributes_must_replace_collection() {
        ConnectorObjectBuilder cob = new ConnectorObjectBuilder();
        cob.setUid(new Uid("uid-123"));
        cob.setName("username123");
        cob.setObjectClass(ObjectClass.GROUP);

        ConnectorObject oldObject = cob.build();

        Set<AttributeDelta> deltas = new HashSet<>();

        AttributeDeltaBuilder attr1 = new AttributeDeltaBuilder();
        attr1.setName(GroupSchemaAttributes.EXTERNAL_ID);
        attr1.addValueToAdd("ext");

        deltas.add(attr1.build());

        Set<Attribute> updated =
                processing.updateObjectAttributes(new Uid("uid-123"), deltas, oldObject);


        Attribute attr = AttributeUtil.find(GroupSchemaAttributes.EXTERNAL_ID, updated);
        assertNotNull(attr);

        List<Object> values = attr.getValue();
        String externalId = values.get(0).toString();

        assertEquals("ext", externalId);

    }

    @Test
    void test_should_thrown_unknown_uid_exception_for_null_connector_object() {
        assertThrows(UnknownUidException.class, () -> processing.updateObjectAttributes(new Uid("uid-123"), new HashSet<>(), null));
    }

    @Test
    void should_extract_id_when_contains_equal_id_prefix() {
        String result = callExtractIdLoose("id=12345;ro=1;hp=0");
        assertEquals("12345", result);
    }

    @Test
    void deveExtrairIdQuandoIdEstaNoFinalSemPontoEVirgula() {
        String result = callExtractIdLoose("some;thing;id=abcde");
        assertEquals("abcde", result);
    }

    @Test
    void deveRetornarStringQuandoEhUUIDOuHex() {
        String hex = "a1b2c3d4e5f6";
        String uuid = "123e4567-e89b-12d3-a456-426614174000";

        assertEquals(hex, callExtractIdLoose(hex));
        assertEquals(uuid, callExtractIdLoose(uuid));
    }

    @Test
    void deveRetornarNullQuandoNaoHaIdENaoEhHex() {
        assertNull(callExtractIdLoose("no id here"));
        assertNull(callExtractIdLoose("abc"));
        assertNull(callExtractIdLoose("xpto;ro=1"));
    }

    @Test
    void deveIgnorarEspacos() {
        String result = callExtractIdLoose("  id=  xyz  ; ro=1");
        assertEquals("xyz", result);
    }

    @Test
    void deveExtrairIdMesmoComLixoAntesOuDepois() {
        String result = callExtractIdLoose("  something id=09876  ;another");
        assertEquals("09876", result);
    }

    @Test
    void deveRetornarNullParaStringVaziaOuNula() {
        assertNull(callExtractIdLoose(""));
        assertNull(callExtractIdLoose("   "));
    }

    @Test
    void should_return_empty_set_when_raw_values_is_null() {
        Set<String> ids = callParseIds(null);
        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    void should_ignore_null_or_empty_values() {
        List<Object> raw = new ArrayList<>();
        raw.add(null);
        raw.add("");
        raw.add("   ");
        raw.add("  ;;; ");

        Set<String> ids = callParseIds(raw);

        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    void should_extract_ids_from_well_formed_strings() {
        List<Object> raw = List.of(
                "id=1;ro=1;hp=0;mg=0",
                "id=2;ro=0;hp=1;mg=0",
                "id=3"
        );

        Set<String> ids = callParseIds(raw);

        assertEquals(List.of("1", "2", "3"), new ArrayList<>(ids));
    }

    @Test
    void should_use_extract_id_loose_when_no_id_present_but_hex_or_uuid() {
        String hex = "a1b2c3d4e5f6";
        String uuid = "123e4567-e89b-12d3-a456-426614174000";

        List<Object> raw = List.of(hex, uuid);

        Set<String> ids = callParseIds(raw);

        assertEquals(2, ids.size());
        assertTrue(ids.contains(hex));
        assertTrue(ids.contains(uuid));
    }

    @Test
    void should_extract_ids_from_strings_with_noise() {
        List<Object> raw = List.of(
                "xxx;id=12345;ro=1",
                "something id=abcde  ; hp=1"
        );

        Set<String> ids = callParseIds(raw);

        assertEquals(2, ids.size());
        assertTrue(ids.contains("12345"));
        assertTrue(ids.contains("abcde"));
    }

    @Test
    void should_not_duplicate_ids_and_preserve_insertion_order() {
        List<Object> raw = List.of(
                "id=1;ro=1",
                "id=2;ro=0",
                "id=1;ro=0;hp=1",
                "a1b2c3d4e5",
                "a1b2c3d4e5"
        );

        Set<String> ids = callParseIds(raw);

        List<String> list = new ArrayList<>(ids);

        assertEquals(3, list.size());
        assertEquals("1", list.get(0));
        assertEquals("2", list.get(1));
        assertEquals("a1b2c3d4e5", list.get(2));
    }

}

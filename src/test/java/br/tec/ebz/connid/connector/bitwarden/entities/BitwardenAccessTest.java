package br.tec.ebz.connid.connector.bitwarden.entities;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenAccessTest {

    private static BitwardenAccess create(String id, Boolean ro, Boolean hp, Boolean mg) {
        BitwardenAccess a = new BitwardenAccess();
        a.setId(id);
        a.setReadOnly(ro);
        a.setHidePassword(hp);
        a.setManage(mg);
        return a;
    }

    @ParameterizedTest
    @CsvSource({
            "A, true,  true,  true,  A, true,  true,  true,  true",
            "A, false, true,  true,  A, false, true,  true,  true",
            "A, true,  false, true,  A, true,  false, true,  true",
            "A, true,  true,  false, A, true,  true,  false, true",

            "A, true,  true,  true,  B, true,  true,  true,  false",
            "A, true,  true,  true,  A, false, true,  true,  false",
            "A, true,  true,  true,  A, true,  false, true,  false",
            "A, true,  true,  true,  A, true,  true,  false, false"
    })
    void testEqualsParametrized(
            String id1, Boolean ro1, Boolean hp1, Boolean mg1,
            String id2, Boolean ro2, Boolean hp2, Boolean mg2,
            boolean expectedEqual
    ) {
        BitwardenAccess a1 = create(id1, ro1, hp1, mg1);
        BitwardenAccess a2 = create(id2, ro2, hp2, mg2);

        assertEquals(expectedEqual, a1.equals(a2));
    }

    static Stream<Arguments> hashCodePairs() {
        return Stream.of(
                Arguments.of((Object) new BitwardenAccess[]{ create("A", true, true, true), create("A", true, true, true) }),
                Arguments.of((Object) new BitwardenAccess[]{ create("B", false, true, false), create("B", false, true, false) }),
                Arguments.of((Object) new BitwardenAccess[]{ create("C", null, null, null), create("C", null, null, null) })
        );
    }

    @ParameterizedTest
    @MethodSource("hashCodePairs")
    void testHashCodeConsistent(BitwardenAccess[] accesses) {
        assertEquals(accesses[0], accesses[1]);
        assertEquals(accesses[0].hashCode(), accesses[1].hashCode());
    }

    @ParameterizedTest
    @CsvSource({
            "A, true,  true,  true,  id=A;ro=1;hp=1;mg=1",
            "B, false, true,  false, id=B;ro=0;hp=1;mg=0",
            "C, true,  false, true,  id=C;ro=1;hp=0;mg=1"
    })
    void testToString(String id, Boolean ro, Boolean hp, Boolean mg, String expected) {
        BitwardenAccess access = create(id, ro, hp, mg);
        assertEquals(expected, access.toString());
    }

    @Test
    public void should_test_equals_class() {
        BitwardenAccess access = create("A", true, true, true);

        assertNotEquals(true, access.equals(new Object()));
    }

    @Test
    void testEqualsWithNullOrDifferentClass() {
        BitwardenAccess a = create("X", true, true, true);

        assertNotEquals(null, a);
    }
}

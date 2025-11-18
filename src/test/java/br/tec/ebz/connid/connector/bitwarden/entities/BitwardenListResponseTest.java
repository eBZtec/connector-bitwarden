package br.tec.ebz.connid.connector.bitwarden.entities;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BitwardenListResponseTest {
    @Test
    void test_getters_and_setters() {
        BitwardenListResponse<String> response = new BitwardenListResponse<>();

        String objectValue = "list";
        List<String> dataValue = List.of("A", "B", "C");

        response.setObject(objectValue);
        response.setData(dataValue);

        assertEquals(objectValue, response.getObject());
        assertEquals(dataValue, response.getData());
    }

    @Test
    void test_generic_type_works() {
        BitwardenListResponse<Integer> intResponse = new BitwardenListResponse<>();

        List<Integer> numbers = List.of(1, 2, 3);
        intResponse.setData(numbers);

        assertEquals(numbers, intResponse.getData());
        assertEquals(3, intResponse.getData().size());
        assertTrue(intResponse.getData().contains(2));
    }
}

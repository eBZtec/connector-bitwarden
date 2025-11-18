package br.tec.ebz.connid.connector.bitwarden.api;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.Phase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BearerAuthInterceptorTest {
    @Test
    void should_set_phase_to_prepare_send() {
        BearerAuthInterceptor interceptor = new BearerAuthInterceptor("dummy-token");
        assertEquals(Phase.PREPARE_SEND, interceptor.getPhase());
    }

    @Test
    void should_add_authorization_header_when_headers_are_null() {
        // arrange
        String token = "my-secret-token";
        BearerAuthInterceptor interceptor = new BearerAuthInterceptor(token);
        Message message = new MessageImpl();

        // garante que não há cabeçalhos inicialmente
        assertNull(message.get(Message.PROTOCOL_HEADERS));

        // act
        interceptor.handleMessage(message);

        // assert
        Map<String, List<String>> headers =
                (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        assertNotNull(headers, "Headers map should have been created");
        assertTrue(headers.containsKey("Authorization"), "Authorization header should be present");

        List<String> authValues = headers.get("Authorization");
        assertNotNull(authValues);
        assertEquals(1, authValues.size());
        assertEquals("Bearer " + token, authValues.get(0));
    }

    @Test
    void should_override_existing_authorization_header_when_headers_already_present() {
        // arrange
        String token = "new-token";
        BearerAuthInterceptor interceptor = new BearerAuthInterceptor(token);
        Message message = new MessageImpl();

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", List.of("Bearer old-token"));
        message.put(Message.PROTOCOL_HEADERS, headers);

        // act
        interceptor.handleMessage(message);

        // assert
        Map<String, List<String>> resultHeaders =
                (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        assertNotNull(resultHeaders);
        assertEquals(1, resultHeaders.get("Authorization").size());
        assertEquals("Bearer " + token, resultHeaders.get("Authorization").get(0));
    }

    @Test
    void should_preserve_other_headers_when_setting_authorization_header() {
        // arrange
        String token = "another-token";
        BearerAuthInterceptor interceptor = new BearerAuthInterceptor(token);
        Message message = new MessageImpl();

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-Custom-Header", List.of("value1", "value2"));
        message.put(Message.PROTOCOL_HEADERS, headers);

        // act
        interceptor.handleMessage(message);

        // assert
        Map<String, List<String>> resultHeaders =
                (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        assertNotNull(resultHeaders);

        // Authorization set corretamente
        assertEquals("Bearer " + token,
                resultHeaders.get("Authorization").get(0));

        // Outros headers preservados
        assertTrue(resultHeaders.containsKey("X-Custom-Header"));
        assertEquals(List.of("value1", "value2"),
                resultHeaders.get("X-Custom-Header"));
    }
}

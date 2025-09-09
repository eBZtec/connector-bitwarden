package br.tec.ebz.connid.connector.bitwarden.it;

import br.tec.ebz.connid.connector.bitwarden.BitwardenConfiguration;
import br.tec.ebz.connid.connector.bitwarden.api.ApiConnectionHandler;
import org.identityconnectors.common.security.GuardedString;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class ApiConnectionHandlerIT {

    private static String env(String key) { return System.getenv(key); }

    private static BitwardenConfiguration configFromEnv() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();
        String base = env("BW_BASE_URL");
        String auth = env("BW_AUTH_URL");
        String id = env("BW_CLIENT_ID");
        String secret = env("BW_CLIENT_SECRET");

        assumeTrue(base != null && !base.isBlank(), "BW_BASE_URL not set");
        assumeTrue(id != null && !id.isBlank(), "BW_CLIENT_ID not set");
        assumeTrue(secret != null && !secret.isBlank(), "BW_CLIENT_SECRET not set");
        assumeTrue(auth != null && !auth.isBlank(), "BW_AUTH_SECRET not set");

        configuration.setHostUrl(base);
        configuration.setClientId(id);
        configuration.setClientSecret(new GuardedString(secret.toCharArray()));
        configuration.setAuthUrl(auth);
        return configuration;
    }

    static class TestableHandler extends ApiConnectionHandler {
        TestableHandler(BitwardenConfiguration configuration) throws MalformedURLException {
            super(configuration);
        }
        <T> T client(Class<T> type) throws MalformedURLException {
            return setupClient(type);
        }
    }

    @Test
    void can_authenticate_and_call_public_api() throws Exception {
        BitwardenConfiguration cfg = configFromEnv();

        TestableHandler handler = new TestableHandler(cfg);

        CollectionsApi api = handler.client(CollectionsApi.class);
        Map<String, Object> result = api.list();

        assertNotNull(result, "Response body should not be null");
        assertEquals("list", result.get("object"), "Expected list wrapper");
        assertTrue(result.containsKey("data"), "Expected data array in response");
    }
}

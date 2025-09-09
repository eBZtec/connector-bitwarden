package br.tec.ebz.connid.connector.bitwarden.api;

import br.tec.ebz.connid.connector.bitwarden.BitwardenConfiguration;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApiConnectionHandlerTest {

    private WireMockServer wm;

    @BeforeEach
    void start() {
        wm = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wm.start();
        configureFor("localhost", wm.port());
    }

    @AfterEach
    void stop() {
        if (wm != null) {
            wm.stop();
        }
    }

    private String baseUrl() {
        return "http://localhost:" + wm.port();
    }

    private String authUrl() {
        return "http://localhost:" + wm.port();
    }

    private BitwardenConfiguration config(String clientId, String clientSecret) {
        BitwardenConfiguration c = new BitwardenConfiguration();
        c.setHostUrl(baseUrl());
        c.setAuthUrl(authUrl());
        c.setClientId(clientId);
        c.setClientSecret(new GuardedString(clientSecret.toCharArray()));
        return c;
    }

    @Test
    void authenticates_via_query_params_and_uses_bearer_header() throws Exception {
        wm.stubFor(post(urlPathEqualTo("/connect/token"))
                .withFormParam("grant_type", equalTo("client_credentials"))
                .withFormParam("scope", equalTo("api.organization"))
                .withFormParam("client_id", equalTo("my-client"))
                .withFormParam("client_secret", equalTo("s3cr3t"))
                .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
                .willReturn(okJson("{\"token\":\"abc123\",\"expiresIn\":3600}")));

        wm.stubFor(get(urlEqualTo("/api/v1/echo"))
                .withHeader("Authorization", equalTo("Bearer abc123"))
                .willReturn(ok("ok")));

        TestableApiConnectionHandler handler =
                new TestableApiConnectionHandler(config("my-client","s3cr3t"));

        EchoService client = handler.newClient(EchoService.class);
        String result = client.echo();
        assertEquals("ok", result);

        wm.verify(1, postRequestedFor(urlPathEqualTo("/connect/token"))
                .withFormParam("grant_type", equalTo("client_credentials"))
                .withFormParam("scope", equalTo("api.organization"))
                .withFormParam("client_id", equalTo("my-client"))
                .withFormParam("client_secret", equalTo("s3cr3t")));
        wm.verify(1, getRequestedFor(urlEqualTo("/api/v1/echo"))
                .withHeader("Authorization", equalTo("Bearer abc123")));
    }

    @Test
    void throws_on_http_failure_status() {
        wm.stubFor(post(urlPathEqualTo("/connect/token"))
                .willReturn(unauthorized().withBody("{\"error\":\"unauthorized\"}")));

        assertThrows(ConnectionFailedException.class,
                () -> new TestableApiConnectionHandler(config("my-client","bad")));
    }

    @Test
    void allows_requests_with_no_chunking_policy() throws Exception {
        wm.stubFor(post(urlPathEqualTo("/connect/token"))
                .willReturn(okJson("{\"token\":\"abc123\",\"expiresIn\":3600}")));

        wm.stubFor(get(urlEqualTo("/api/v1/echo"))
                .willReturn(ok("ok")));

        TestableApiConnectionHandler handler =
                new TestableApiConnectionHandler(config("my-client","s3cr3t"));
        EchoService client = handler.newClient(EchoService.class);

        assertEquals("ok", client.echo());
    }

    @Test
    void throws_on_http_not_found_url() {
        wm.stubFor(
                post(urlPathEqualTo("/notfound/url")).willReturn(notFound())
        );

        assertThrows(ConnectionFailedException.class, () -> new TestableApiConnectionHandler(config("my-client","s3cr3t")));
    }
}

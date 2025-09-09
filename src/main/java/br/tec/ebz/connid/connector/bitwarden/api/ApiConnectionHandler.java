package br.tec.ebz.connid.connector.bitwarden.api;

import br.tec.ebz.connid.connector.bitwarden.BitwardenConfiguration;
import com.evolveum.polygon.common.GuardedStringAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public abstract class ApiConnectionHandler {
    private static final Log LOG = Log.getLog(ApiConnectionHandler.class);
    private static final String AUTH_URL = "/connect/token";

    private BitwardenConfiguration configuration;

    private String token;
    private Integer expiresIn;

    public ApiConnectionHandler(BitwardenConfiguration configuration) throws MalformedURLException {
        this.configuration = configuration;
        fetchToken();
    }

    protected <T> T setupClient(Class<T> type) throws MalformedURLException {
        JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        bean.setResourceClass(type);
        bean.setAddress(this.configuration.getHostUrl());

        JacksonJsonProvider provider = new JacksonJsonProvider();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.findAndRegisterModules();
        provider.setMapper(mapper);

        bean.setProvider(provider);
        bean.getOutInterceptors().add(new BearerAuthInterceptor(getAccessToken()));

        T proxy = bean.create(type);

        ClientConfiguration config = WebClient.getConfig(proxy);

        HTTPConduit conduit = config.getHttpConduit();
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setAllowChunking(false);

        conduit.setClient(policy);

        return proxy;
    }

    private void fetchToken() throws MalformedURLException {
        URI baseUri = URI.create(configuration.getAuthUrl());
        URI resolvedUri = baseUri.resolve(AUTH_URL);

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.setMapper(mapper);

        WebClient client = WebClient.create(String.valueOf(resolvedUri), List.of(provider));
        client.accept("application/json");
        client.type("application/x-www-form-urlencoded");

        GuardedStringAccessor accessor = new GuardedStringAccessor();
        this.configuration.getClientSecret().access(accessor);

        var form = new Form();
        form.param("grant_type", "client_credentials");
        form.param("scope", "api.organization");
        form.param("client_id", configuration.getClientId());
        form.param("client_secret", accessor.getClearString());

        Response response = client.post(form);

        if (response.getStatus() < 200 || response.getStatus() >= 300) {
            LOG.error("Could not authenticated, reason: " + response.getStatusInfo().getReasonPhrase());
            throw new ConnectionFailedException("Could not not authenticate, reason: " + response.getStatusInfo().getReasonPhrase());
        }

        LOG.ok("Authentication worked successfully.");

        Map<String, Object> responseBody = response.readEntity(new GenericType<>() {});

        String accessToken = (String) responseBody.getOrDefault("access_token", responseBody.get("token"));
        Number exp = (Number) responseBody.getOrDefault("expires_in", responseBody.get("expiresIn"));

        token = accessToken;
        expiresIn = exp != null ? exp.intValue() : null;

    }

    private String getAccessToken() {
        return token;
    }

}

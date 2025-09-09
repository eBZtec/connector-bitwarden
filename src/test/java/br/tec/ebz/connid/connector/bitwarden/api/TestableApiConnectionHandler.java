package br.tec.ebz.connid.connector.bitwarden.api;

import br.tec.ebz.connid.connector.bitwarden.BitwardenConfiguration;

import java.net.MalformedURLException;

public class TestableApiConnectionHandler extends ApiConnectionHandler{
    public TestableApiConnectionHandler(BitwardenConfiguration configuration) throws MalformedURLException {
        super(configuration);
    }

    public <T> T newClient(Class<T> type) throws MalformedURLException {
        return setupClient(type);
    }
}

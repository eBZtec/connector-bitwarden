package br.tec.ebz.connid.connector.bitwarden;

import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ResultsHandler;

import java.util.ArrayList;
import java.util.List;

public class ListResultHandler implements ResultsHandler {
    private List<ConnectorObject> objects = new ArrayList<>();

    @Override
    public boolean handle(ConnectorObject object) {
        objects.add(object);

        return true;
    }

    public List<ConnectorObject> getObjects() {
        return objects;
    }
}

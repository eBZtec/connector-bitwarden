package br.tec.ebz.connid.connector.bitwarden;

import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SchemaGroupIT extends BitwardenConfigurationHandler{
    private ConnectorFacade facade;

    @BeforeEach
    public void init() {
        facade = getTestConnection();
    }

    @Test
    public void should_build_schema_groups() {
        Schema schemas = facade.schema();

        assertEquals(3, schemas.getObjectClassInfo().size());
    }
}

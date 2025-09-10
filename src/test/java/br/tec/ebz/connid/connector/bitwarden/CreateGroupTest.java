package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CreateGroupTest extends BitwardenConfigurationHandler{

    private String name;

    @BeforeEach
    public void generateId() {
        int randomCode = new Random().nextInt(1000);
        name = "Test Group" + randomCode;
    }

    @Test
    void should_create_valid_group() {
        ConnectorFacade facade = getTestConnection();

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, name));
        attributes.add(AttributeBuilder.build(GroupSchemaAttributes.EXTERNAL_ID, name));

        Uid uid = facade.create(GroupsProcessing.OBJECT_CLASS, attributes, null);

        assertNotNull(uid, "Group uid cannot be null on creation");

        ListResultHandler handler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(Uid.NAME, uid.getUidValue());
        EqualsFilter filter = new EqualsFilter(attribute);

        facade.search(GroupsProcessing.OBJECT_CLASS, filter, handler, null);

        List<ConnectorObject> objects = handler.getObjects();

        assertEquals(1, objects.size());

        facade.delete(GroupsProcessing.OBJECT_CLASS, uid, null);
    }
}

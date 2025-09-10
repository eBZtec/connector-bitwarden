package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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

        assertNotNull(uid, "Uid cannot be null");

        facade.delete(GroupsProcessing.OBJECT_CLASS, uid, null);
    }
}

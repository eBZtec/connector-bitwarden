package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CreateMemberTest extends BitwardenConfigurationHandler{


    private String email;
    private String name;
    private String login;
    private static final String password = "smartway";

    @BeforeEach
    public void generateId() {
        int randomCode = new Random().nextInt(1000);
        name = "Test User" + randomCode;
        login = "test.user" + randomCode;
        email = login + "@example.com";
    }

    @Test
    void should_create_valid_member_without_group() {
        ConnectorFacade facade = getTestConnection();

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, email));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.NAME, name));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TWO_FACTOR_ENABLED, false));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.STATUS, 0));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.RESET_PASSWORD_ENROLLED, false));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.SSO_EXTERNAL_ID, login));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TYPE, 1));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.GROUPS, new ArrayList<>()));

        Uid uid = facade.create(MemberProcessing.OBJECT_CLASS, attributes, null);

        assertNotNull(uid, "Uid cannot be null");

        facade.delete(MemberProcessing.OBJECT_CLASS, uid, null);
    }
}

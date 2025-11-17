package br.tec.ebz.connid.connector.bitwarden.repository;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

import java.util.HashSet;
import java.util.Set;

public class MemberRepository {
    private final ConnectorFacade connectorFacade;

    public MemberRepository(ConnectorFacade connectorFacade) {
        this.connectorFacade = connectorFacade;
    }

    public Uid create(BitwardenMember member) {

        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(Name.NAME, member.getEmail()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.NAME, member.getName()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TWO_FACTOR_ENABLED, member.getTwoFactorEnabled()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.STATUS, member.getStatus()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.RESET_PASSWORD_ENROLLED, member.getResetPasswordEnrolled()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.SSO_EXTERNAL_ID, member.getSsoExternalId()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.TYPE, member.getType()));
        attributes.add(AttributeBuilder.build(MemberSchemaAttributes.GROUPS, member.getGroups()));

        return connectorFacade.create(MemberProcessing.OBJECT_CLASS, attributes, null);
    }
}

package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.services.MembersService;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;

import java.util.List;
import java.util.Set;

public class MemberProcessing extends ObjectProcessing {

    private static final Log LOG = Log.getLog(MemberProcessing.class);

    public static final String OBJECT_CLASS_NAME = "member";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    private final MembersService membersService;

    public MemberProcessing(MembersService membersService) {
        this.membersService = membersService;
    }

    public Uid create(Set<Attribute> attributes, OperationOptions operationOptions) {
        BitwardenMember member = translate(attributes);
        BitwardenMember memberResponse = membersService.create(member);

        String memberId = memberResponse.getId();
        LOG.ok("Member \"{0}\" created successfully.", memberId);

        return new Uid(memberId);
    }

    public void update(Uid uid, Set<AttributeDelta> attributeDeltas, OperationOptions operationOptions) {
        ConnectorObject currentObject = getObject(uid.getUidValue());
        Set<Attribute> updatedAttributes = updateObjectAttributes(uid, attributeDeltas, currentObject);
        BitwardenMember member = translate(updatedAttributes);

        BitwardenMember updatedMember = membersService.update(uid.getUidValue(), member);

        LOG.ok("Member \"{0}\" updated successfully.", uid.getUidValue());
    }

    public void delete(Uid uid, OperationOptions options) {
        membersService.delete(uid.getUidValue());
        LOG.ok("Member \"{0}\" deleted successfully.", uid.getUidValue());
    }

    private ConnectorObject getObject(String id) {
        BitwardenMember member = membersService.get(id);

        if (member == null) throw new UnknownUidException("Member id \"" + id + "\" does not exists");

        return translate(member);
    }

    private BitwardenMember translate(Set<Attribute> attributes) {
        String id = getAttributeValue(Uid.NAME, String.class, attributes);
        String email = getAttributeValue(Name.NAME, String.class, attributes);
        String name = getAttributeValue(MemberSchemaAttributes.NAME, String.class, attributes);
        Boolean twoFactorEnabled = getAttributeValue(MemberSchemaAttributes.TWO_FACTOR_ENABLED, Boolean.class, attributes);
        Integer status = getAttributeValue(MemberSchemaAttributes.STATUS, Integer.class, attributes);
        Boolean resetPasswordEnrolled = getAttributeValue(MemberSchemaAttributes. RESET_PASSWORD_ENROLLED, Boolean.class, attributes);
        String ssoExternalId = getAttributeValue(MemberSchemaAttributes.SSO_EXTERNAL_ID, String.class, attributes);
        Integer type = getAttributeValue(MemberSchemaAttributes.TYPE, Integer.class, attributes);
        String externalId = getAttributeValue(MemberSchemaAttributes.EXTERNAL_ID, String.class, attributes);
        List<String> groups = getAttributeValue(MemberSchemaAttributes.GROUPS, List.class, attributes);

        BitwardenMember member = new BitwardenMember();

        if (id != null) member.setId(id);

        member.setEmail(email);
        member.setName(name);
        member.setTwoFactorEnabled(twoFactorEnabled);
        member.setStatus(status);
        member.setResetPasswordEnrolled(resetPasswordEnrolled);
        member.setSsoExternalId(ssoExternalId);
        member.setType(type);
        member.setExternalId(externalId);
        member.setGroups(groups);
        member.setObject("member");

        LOG.info("Member defined as {0}", member);

        return member;
    }

    private ConnectorObject translate(BitwardenMember member) {
        ConnectorObjectBuilder connectorObject = new ConnectorObjectBuilder();
        connectorObject.setObjectClass(MemberProcessing.OBJECT_CLASS);

        addAttribute(connectorObject, Uid.NAME, member.getId());
        addAttribute(connectorObject, Name.NAME, member.getEmail());
        addAttribute(connectorObject, MemberSchemaAttributes.TWO_FACTOR_ENABLED, member.getTwoFactorEnabled());
        addAttribute(connectorObject, MemberSchemaAttributes.STATUS, member.getStatus());
        addAttribute(connectorObject, MemberSchemaAttributes.RESET_PASSWORD_ENROLLED, member.getResetPasswordEnrolled());
        addAttribute(connectorObject, MemberSchemaAttributes.SSO_EXTERNAL_ID, member.getExternalId());
        addAttribute(connectorObject, MemberSchemaAttributes.TYPE, member.getType());
        addAttribute(connectorObject, MemberSchemaAttributes.EXTERNAL_ID, member.getExternalId());
        addAttribute(connectorObject, MemberSchemaAttributes.GROUPS, member.getGroups());

        return connectorObject.build();
    }

}

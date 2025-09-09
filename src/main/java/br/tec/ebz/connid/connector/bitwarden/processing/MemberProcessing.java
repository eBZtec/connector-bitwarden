package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.services.MembersService;
import org.identityconnectors.common.logging.Log;
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

    public void delete(Uid uid, OperationOptions options) {
        membersService.delete(uid.getUidValue());
        LOG.ok("Member \"{0}\" deleted successfully.", uid.getUidValue());
    }

    private BitwardenMember translate(Set<Attribute> attributes) {
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


}

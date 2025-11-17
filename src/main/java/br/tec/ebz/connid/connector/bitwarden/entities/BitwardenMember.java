package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.List;

public record BitwardenMember(
        String object,
        String id,
        String name,
        String email,
        Boolean twoFactorEnabled,
        Integer status,
        List<BitwardenCollection> collections,
        Boolean resetPasswordEnrolled,
        String ssoExternalId,
        Integer type,
        String externalId,
        List<String> groups,
        BitwardenPermissions permissions
) {
    public BitwardenMember withGroups(List<String> newGroups) {
        return new BitwardenMember(
                object,
                id,
                name,
                email,
                twoFactorEnabled,
                status,
                collections,
                resetPasswordEnrolled,
                ssoExternalId,
                type,
                externalId,
                newGroups,
                permissions
        );
    }
}

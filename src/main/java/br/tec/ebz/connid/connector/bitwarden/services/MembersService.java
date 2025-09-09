package br.tec.ebz.connid.connector.bitwarden.services;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/public/members")
public interface MembersService {

    @POST
    @Path("")
    BitwardenMember create(BitwardenMember member);
}

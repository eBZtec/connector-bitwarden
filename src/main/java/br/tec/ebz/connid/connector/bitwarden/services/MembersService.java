package br.tec.ebz.connid.connector.bitwarden.services;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/public/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MembersService {

    @POST
    @Path("")
    BitwardenMember create(BitwardenMember member);
}

package br.tec.ebz.connid.connector.bitwarden.services;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/public/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MembersService {

    @POST
    @Path("")
    BitwardenMember create(BitwardenMember member);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id);

    @GET
    @Path("/{id}")
    BitwardenMember get(@PathParam("id") String id);
}

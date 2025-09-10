package br.tec.ebz.connid.connector.bitwarden.services;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import br.tec.ebz.connid.connector.bitwarden.entities.BiwardenUpdateMemberGroups;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

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

    @PUT
    @Path("/{id}")
    BitwardenMember update(@PathParam("id") String id, BitwardenMember member);

    @PUT
    @Path("/{id}/group-ids")
    void updateMemberGroups(@PathParam("id") String id, BiwardenUpdateMemberGroups body);

    @GET
    @Path("/{id}/group-ids")
    List<String> getMemberGroups(@PathParam("id") String id);
}

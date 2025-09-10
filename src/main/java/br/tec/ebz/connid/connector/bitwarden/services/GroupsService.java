package br.tec.ebz.connid.connector.bitwarden.services;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenGroup;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenListResponse;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenMember;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/public/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GroupsService {

    @POST
    @Path("")
    BitwardenGroup create(BitwardenGroup group);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id);

    @GET
    @Path("/{id}")
    BitwardenGroup get(@PathParam("id") String id);

    @PUT
    @Path("/{id}")
    BitwardenGroup update(@PathParam("id") String id, BitwardenGroup group);

    @GET
    @Path("/{id}/group-ids")
    List<String> getGroupMembers(@PathParam("id") String id);

    @GET
    @Path("")
    BitwardenListResponse<BitwardenGroup> list();
}

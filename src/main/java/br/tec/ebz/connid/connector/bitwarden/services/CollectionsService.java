package br.tec.ebz.connid.connector.bitwarden.services;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenCollection;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenGroup;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenListResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/public/collections")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CollectionsService {

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id);

    @GET
    @Path("/{id}")
    BitwardenCollection get(@PathParam("id") String id);

    @PUT
    @Path("/{id}")
    BitwardenCollection update(@PathParam("id") String id, BitwardenCollection collection);

    @GET
    @Path("")
    BitwardenListResponse<BitwardenCollection> list();
}

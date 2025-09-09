package br.tec.ebz.connid.connector.bitwarden.it;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/public/collections")
public interface CollectionsApi {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> list();
}

package br.tec.ebz.connid.connector.bitwarden.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/echo")
public interface EchoService {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String echo();
}

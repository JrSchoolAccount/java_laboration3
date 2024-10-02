package org.laboration2;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/hello-world")
public class AppResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }
}
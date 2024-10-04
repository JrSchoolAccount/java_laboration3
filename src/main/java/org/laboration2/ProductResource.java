package org.laboration2;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.laboration2.warehouse.entities.Product;
import org.laboration2.warehouse.service.Warehouse;

import java.time.LocalDate;

@Path("/products")
public class ProductResource {

    @Inject
    private Warehouse warehouse;


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProduct(Product product) {
        try {
            LocalDate date = LocalDate.now();

            warehouse.newProduct(
                    product.id(),
                    product.name(),
                    product.type(),
                    product.rating(),
                    date,
                    date
            );
            return Response.status(Response.Status.CREATED).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
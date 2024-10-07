package org.laboration2;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.laboration2.warehouse.entities.Product;
import org.laboration2.warehouse.entities.ProductType;
import org.laboration2.warehouse.service.Warehouse;

import java.time.LocalDate;
import java.util.List;

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

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response allProducts() {
        try {
            List<Product> products = warehouse.getAllProducts();

            return Response.status(Response.Status.OK).entity(products).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProduct(@PathParam("id") int id) {
        return warehouse.getProductById(id)
                .map(product -> Response.status(Response.Status.OK).entity(product).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity("Product with id: " + id + " not found")
                        .build());
    }

    @GET
    @Path("/category/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allProductsInCategory(@PathParam("category") String category) {
        try {
            ProductType productType = ProductType.valueOf(category.toUpperCase());

            List<Product> products = warehouse.getProductsByTypeSortedAtoZ(productType);

            return Response.ok(products).build();
        } catch (IllegalArgumentException e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid product category: " + category)
                    .build();
        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while fetching products")
                    .build();
        }
    }
}
package org.laboration3.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.laboration3.entities.Product;
import org.laboration3.entities.ProductType;
import org.laboration3.service.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Path("/products")
public class ProductResource {

    private static final Logger logger = LoggerFactory.getLogger(ProductResource.class);

    private Warehouse warehouse;

    public ProductResource() {}

    @Inject
    public ProductResource(Warehouse warehouse) {
        this.warehouse = warehouse;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProduct(@Valid Product product) {
        logger.info("Trying to add product: {}", product.name());
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
            logger.info("Product {} added", product.name());
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response allProducts(@QueryParam("page") int page, @QueryParam("size") int size) {
        logger.info("Trying to list all products");
        try {
            List<Product> products;

            if (page !=0 && size !=0) {
                logger.info("Listing all products on page {} and with page size {}", page, size);
                products = warehouse.paginateAllProducts(page, size);
            } else {
                products = warehouse.getAllProducts();
            }

            logger.info("All Products successfully listed");
            return Response.status(Response.Status.OK).entity(products).build();
        } catch (Exception e) {
            logger.error("Failed to list all products: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProduct(@PathParam("id") int id) {
        logger.info("Trying to find product with id: {}", id);

        return warehouse.getProductById(id)
                .map(product -> {
                    logger.info("Product with id: {} found: {}", id, product);
                    return Response.status(Response.Status.OK).entity(product).build();
                })
                .orElseGet(() -> {
                    logger.warn("Product with id: {} not found", id);
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Product with id: " + id + " not found")
                            .build();
                });
    }

    @GET
    @Path("/category/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allProductsInCategory(@PathParam("category") String category) {
        logger.info("Trying to list all products in category: {}", category);

        try {
            ProductType productType = ProductType.valueOf(category.toUpperCase());

            List<Product> products = warehouse.getProductsByTypeSortedAtoZ(productType);

            logger.info("Products successfully listed");
            return Response.ok(products).build();
        } catch (IllegalArgumentException e) {

            logger.error("{}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid product category: " + category)
                    .build();
        } catch (Exception e) {

            logger.error("An unexpected error occurred while fetching products: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while fetching products")
                    .build();
        }
    }
}
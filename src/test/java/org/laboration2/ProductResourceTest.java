package org.laboration2;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.laboration2.warehouse.entities.Product;
import org.laboration2.warehouse.entities.ProductType;
import org.laboration2.warehouse.service.Warehouse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductResourceTest {

    @Mock
    private Warehouse warehouse;

    @InjectMocks
    private ProductResource productResource;

    private final LocalDate now = LocalDate.now();
    private Product validProduct;

    @BeforeEach
    public void setup() {
        validProduct = new Product(1, "Long sword", ProductType.WEAPON, 8, now, now);
    }

    @Test
    public void testAddProduct_Success() {

        doNothing().when(warehouse).newProduct(
                validProduct.id(),
                validProduct.name(),
                validProduct.type(),
                validProduct.rating(),
                now,
                now
        );

        Response response = productResource.addProduct(validProduct);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        verify(warehouse, times(1)).newProduct(
                validProduct.id(),
                validProduct.name(),
                validProduct.type(),
                validProduct.rating(),
                LocalDate.now(),
                LocalDate.now()
        );
    }

    @Test
    public void testAddProduct_BadRequest() {

        doThrow(new IllegalArgumentException("Product name cannot be null or empty")).when(warehouse).newProduct(
                anyInt(),
                anyString(),
                any(ProductType.class),
                anyInt(),
                any(LocalDate.class),
                any(LocalDate.class)
        );

        Product invalidProduct = new Product(2, "", ProductType.ARTIFACT, 5, LocalDate.now(), LocalDate.now());

        Response response = productResource.addProduct(invalidProduct);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        verify(warehouse, times(1)).newProduct(
                invalidProduct.id(),
                invalidProduct.name(),
                invalidProduct.type(),
                invalidProduct.rating(),
                LocalDate.now(),
                LocalDate.now()
        );
    }
}

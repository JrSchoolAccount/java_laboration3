package org.laboration2;

import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.laboration2.entities.Product;
import org.laboration2.entities.ProductType;
import org.laboration2.resource.ProductResource;
import org.laboration2.service.Warehouse;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductResourceTest {

    private Warehouse warehouse;
    private Dispatcher dispatcher;

    @BeforeEach
    public void setup() {
        warehouse = Mockito.mock(Warehouse.class);
        ProductResource productResource = new ProductResource(warehouse);

        dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(productResource);
    }

    @Test
    void whenPostingValidProductThenShouldReturn201Created() throws URISyntaxException {
        String json = """
    {
        "id": 1,
        "name": "Necronomicon",
        "type": "ARTIFACT",
        "rating": 7
    }
    """;

        MockHttpRequest request = MockHttpRequest.post("/products")
                .content(json.getBytes())
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        assertEquals(201, response.getStatus());
    }

    @Test
    void allProductsGivesJsonArray() throws URISyntaxException, UnsupportedEncodingException, JSONException {
        LocalDate date = LocalDate.of(2024, 3,10);
        System.out.println("LocalDate: " + date);
        List<Product> mockedProducts = List.of(
                new Product(1, "Morning star", ProductType.WEAPON, 2, date, date),
                new Product(2, "Shiv", ProductType.WEAPON, 3, date, date),
                new Product(3, "Broad sword", ProductType.WEAPON, 4, date, date)
        );

        System.out.println("mockedProducts: " + mockedProducts);

        Mockito.when(warehouse.getAllProducts()).thenReturn(mockedProducts);

        MockHttpRequest request = MockHttpRequest.get("/products");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(200, response.getStatus());

        String jsonBody = response.getContentAsString();
        System.out.println("JSON Response: " + jsonBody);

        String expectedJsonResponse = """
            [
                {
                    "id": 1,
                    "name": "Morning star",
                    "rating": 2,
                    "type": "WEAPON",
                    "created": "2024-10-14",
                    "modified": "2024-10-14"
                },
                {
                    "id": 2,
                    "name": "Shiv",
                    "rating": 3,
                    "type": "WEAPON",
                    "created": "2024-10-14",
                    "modified": "2024-10-14"
                },
                {
                    "id": 3,
                    "name": "Broad sword",
                    "rating": 4,
                    "type": "WEAPON",
                    "created": "2024-10-14",
                    "modified": "2024-10-14"
                }
            ]
        """;

        JSONAssert.assertEquals(expectedJsonResponse, jsonBody, JSONCompareMode.LENIENT);
    }
}

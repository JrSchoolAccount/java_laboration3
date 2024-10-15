package org.laboration3;

import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.laboration3.entities.Product;
import org.laboration3.entities.ProductType;
import org.laboration3.resource.ProductResource;
import org.laboration3.service.Warehouse;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;

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
}

package org.laboration2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.laboration2.resource.ProductResource;
import org.laboration2.service.Warehouse;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

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
    void whenPostingValidProductThenShouldReturn201Created() throws URISyntaxException, UnsupportedEncodingException {
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

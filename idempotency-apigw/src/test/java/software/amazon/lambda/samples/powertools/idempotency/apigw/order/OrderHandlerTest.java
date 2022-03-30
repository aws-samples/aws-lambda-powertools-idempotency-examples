/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package software.amazon.lambda.samples.powertools.idempotency.apigw.order;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.lambda.powertools.idempotency.Idempotency;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dao.OrderDao;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.mapper.OrderRequestMapper;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.entity.Order;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderHandlerTest {

    OrderRequestMapper orderRequestMapper = new OrderRequestMapper();

    @Mock
    Context context;

    @Mock
    OrderDao orderDao;

    @Mock
    Idempotency idempotency;

    OrderHandler handler;

    @BeforeEach
    public void setUp() {
       handler = new OrderHandler(orderDao, orderRequestMapper, idempotency);
    }

    @Test
    public void shouldReturnStatus201() {
        // Given
        String body = "{\"userId\": \"user1\", \"items\": [{\"productId\": \"product1\", \"price\": 15.50, \"quantity\": 1}]}";
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withBody(body);
        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
        // Then
        Assertions.assertEquals(201, response.getStatusCode());
    }

    @Test
    public void shouldReturnOrderId() throws JsonProcessingException {
        // Given
        String body = "{\"userId\": \"user1\", \"items\": [{\"productId\": \"product1\", \"price\": 15.50, \"quantity\": 1}]}";
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withBody(body);
        String expectedOrderId = UUID.randomUUID().toString();
        when(orderDao.create(any(Order.class))).thenReturn(expectedOrderId);
        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
        // Then
        JsonNode bodyJsonNode = new ObjectMapper().readValue(response.getBody(), JsonNode.class);
        Assertions.assertNotNull(bodyJsonNode.get("orderId"));
        Assertions.assertEquals(expectedOrderId, bodyJsonNode.get("orderId").asText());
    }
}
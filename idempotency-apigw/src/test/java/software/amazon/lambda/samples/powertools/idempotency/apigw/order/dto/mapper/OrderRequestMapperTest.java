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
package software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.OrderRequest;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.OrderRequestItem;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.entity.Order;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.entity.OrderItem;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

class OrderRequestMapperTest {

    OrderRequest orderRequest;
    OrderRequestMapper mapper;

    @BeforeEach
    public void setUp() {
        // Given
        orderRequest = new OrderRequest();
        orderRequest.setUserId("userId");
        orderRequest.setComment("comment");
        OrderRequestItem item1 = new OrderRequestItem();
        item1.setProductId("product1");
        item1.setPrice(BigDecimal.valueOf(5.25));
        item1.setQuantity(1);
        OrderRequestItem item2 = new OrderRequestItem();
        item2.setProductId("product2");
        item2.setPrice(BigDecimal.valueOf(12.50));
        item2.setQuantity(2);
        orderRequest.setItems(List.of(item1, item2));

        mapper = new OrderRequestMapper();
    }

    @Test
    public void shouldMapOrderDetails() {
        // When
        Order order = mapper.map(orderRequest);
        // Then
        Assertions.assertEquals(orderRequest.getUserId(), order.getUserId());
        Assertions.assertEquals(orderRequest.getComment(), order.getComment());
    }

    @Test
    public void shouldMapOrderItems() {
        // When
        Order order = mapper.map(orderRequest);
        // Then
        Assertions.assertEquals(orderRequest.getItems().size(), order.getItems().size());
        Iterator<OrderItem> orderItemIterator = order.getItems().iterator();
        orderRequest.getItems().forEach(orderRequestItem -> {
            OrderItem orderItem = orderItemIterator.next();
            Assertions.assertEquals(orderRequestItem.getProductId(), orderItem.getProductId());
            Assertions.assertEquals(orderRequestItem.getPrice(), orderItem.getPrice());
            Assertions.assertEquals(orderRequestItem.getQuantity(), orderItem.getQuantity());
        });
    }
}
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
package software.amazon.lambda.samples.powertools.idempotency.apigw.order.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.entity.Order;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderDaoTest {

    @Test
    public void shouldGenerateOrderId(@Mock DynamoDbTable<Order> ordersTable) {
        // Given
        OrderDao orderDao = new OrderDao(ordersTable);
        Order order = Order.builder().build();
        // When
        String orderId = orderDao.create(order);
        // Then
        Assertions.assertNotNull(orderId);
        Assertions.assertFalse(orderId.isEmpty());
        Assertions.assertEquals(orderId, order.getOrderId());
    }

    @Test
    public void shouldCallPutItem(@Mock DynamoDbTable<Order> ordersTable) {
        // Given
        OrderDao orderDao = new OrderDao(ordersTable);
        Order order = Order.builder().build();
        // When
        orderDao.create(order);
        // Then
        verify(ordersTable, times(1)).putItem(order);
    }
}
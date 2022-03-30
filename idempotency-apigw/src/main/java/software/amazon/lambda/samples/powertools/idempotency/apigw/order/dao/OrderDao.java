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

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.entity.Order;

import java.util.UUID;

public class OrderDao {

    private final DynamoDbTable<Order> ordersTable;

    public OrderDao(DynamoDbTable<Order> ordersTable) {
        this.ordersTable = ordersTable;
    }

    public String create(Order order) {
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        ordersTable.putItem(order);
        return orderId;
    }
}

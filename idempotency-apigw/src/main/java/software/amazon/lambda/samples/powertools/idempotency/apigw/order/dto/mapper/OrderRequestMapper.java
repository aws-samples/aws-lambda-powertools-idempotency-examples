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

import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.OrderRequest;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.OrderRequestItem;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.entity.Order;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.entity.OrderItem;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class OrderRequestMapper {

    @Inject
    public OrderRequestMapper() {
        //required by Dagger
    }

    public Order map(OrderRequest orderRequest) {
            return Order.builder()
                    .userId(orderRequest.getUserId())
                    .comment(orderRequest.getComment())
                    .items(map(orderRequest.getItems()))
                    .build();

    }

    private List<OrderItem> map(List<OrderRequestItem> orderRequestItems) {
        return orderRequestItems.stream().map(this::map).collect(Collectors.toList());
    }

    private OrderItem map(OrderRequestItem orderRequestItem) {
        return OrderItem.builder()
                .productId(orderRequestItem.getProductId())
                .price(orderRequestItem.getPrice())
                .quantity(orderRequestItem.getQuantity())
                .build();
    }
}

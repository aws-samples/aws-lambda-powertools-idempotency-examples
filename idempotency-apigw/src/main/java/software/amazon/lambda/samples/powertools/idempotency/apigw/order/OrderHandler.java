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
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import software.amazon.lambda.powertools.idempotency.Idempotency;
import software.amazon.lambda.powertools.idempotency.Idempotent;
import software.amazon.lambda.powertools.utilities.JsonConfig;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dao.OrderDao;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.di.DaggerOrderHandlerComponent;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.OrderRequest;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.OrderResponse;
import software.amazon.lambda.samples.powertools.idempotency.apigw.order.dto.mapper.OrderRequestMapper;

import javax.inject.Inject;
import java.util.Map;

import static software.amazon.lambda.powertools.utilities.EventDeserializer.extractDataFrom;

@AllArgsConstructor
public class OrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    OrderDao orderDao;

    @Inject
    OrderRequestMapper orderRequestMapper;

    @Inject
    Idempotency idempotency;

    public OrderHandler() {
        DaggerOrderHandlerComponent.builder().build().inject(this);
    }

    @Idempotent
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        OrderRequest orderRequest = extractDataFrom(input.getBody()).as(OrderRequest.class);
        String orderId = orderDao.create(orderRequestMapper.map(orderRequest));
        return buildSuccessfulResponse(new OrderResponse(orderId));
    }

    private <T> APIGatewayProxyResponseEvent buildSuccessfulResponse(T body) {
        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    .withBody(JsonConfig.get().getObjectMapper().writeValueAsString(body));
        }
        catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
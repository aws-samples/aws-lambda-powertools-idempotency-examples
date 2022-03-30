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
package software.amazon.lambda.samples.powertools.idempotency.sqs;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.PaymentHandler;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.dto.PaymentRequest;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.service.PaymentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentHandlerTest {

    @Mock
    Context context;

    @Mock
    PaymentService paymentService;

    PaymentHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PaymentHandler(paymentService);
    }

    @Test
    public void shouldReturnPaymentIds() {
        //Given
        String expectedIdPayment1 = "payment1";
        String expectedIdPayment2 = "payment2";
        when(paymentService.process(any(PaymentRequest.class)))
                .thenReturn(expectedIdPayment1)
                .thenReturn(expectedIdPayment2);
        SQSEvent request = new SQSEvent();
        SQSEvent.SQSMessage message1 = new SQSEvent.SQSMessage();
        message1.setBody("{\"orderId\": \"order1\", \"userId\": \"user1\", \"amount\": 100}");
        SQSEvent.SQSMessage message2 = new SQSEvent.SQSMessage();
        message2.setBody("{\"orderId\": \"order2\", \"userId\": \"user2\", \"amount\": 50.25}");
        request.setRecords(List.of(message1, message2));
        //When
        List<String> paymentIds = handler.handleRequest(request, context);
        //Then
        Assertions.assertEquals(2, paymentIds.size());
        Assertions.assertEquals(expectedIdPayment1, paymentIds.get(0));
        Assertions.assertEquals(expectedIdPayment2, paymentIds.get(1));
    }
}
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
package software.amazon.lambda.samples.powertools.idempotency.sqs.payment.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.api.PaymentApi;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.dto.PaymentRequest;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.exception.PaymentException;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    PaymentRequest paymentRequest;

    @Mock
    PaymentApi paymentApi;

    PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        paymentService = new PaymentService(paymentApi);
        //Given
        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId("order1");
        paymentRequest.setUserId("user1");
        paymentRequest.setAmount(BigDecimal.valueOf(50.25));
    }

    @Test
    public void shouldReturnPaymentId() {
        //Given
        String expectedIdPayment1 = "payment1";
        when(paymentApi.postPayment(paymentRequest)).thenReturn(expectedIdPayment1);
        //When
        String paymentId = paymentService.process(paymentRequest);
        //Then
        Assertions.assertEquals(expectedIdPayment1, paymentId);
    }

    @Test
    public void shouldThrowException() {
        //Given
        when(paymentApi.postPayment(paymentRequest)).thenThrow(PaymentException.class);
        //When && Then
        Assertions.assertThrows(PaymentException.class, () -> paymentService.process(paymentRequest));
    }
}
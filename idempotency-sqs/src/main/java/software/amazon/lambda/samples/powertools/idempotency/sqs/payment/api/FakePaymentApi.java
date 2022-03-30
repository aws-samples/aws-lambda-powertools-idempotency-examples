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
package software.amazon.lambda.samples.powertools.idempotency.sqs.payment.api;

import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.dto.PaymentRequest;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.exception.PaymentException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class FakePaymentApi implements PaymentApi {

    private static final int MAX_DELAY_IN_MILLIS = 5000;
    private static final double FAILURE_RATIO = 0.2;

    @Inject
    public FakePaymentApi() {
        //needed by Dagger
    }

    @Override
    public String postPayment(PaymentRequest paymentRequest) throws PaymentException {
        sleep();
        if (Math.random() < FAILURE_RATIO) {
            throw new PaymentException("Payment failed");
        }
        return UUID.randomUUID().toString();
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(MAX_DELAY_IN_MILLIS));
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }
}

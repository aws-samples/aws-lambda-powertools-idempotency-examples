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
package software.amazon.lambda.samples.powertools.idempotency.sqs.payment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.idempotency.Idempotency;
import software.amazon.lambda.powertools.idempotency.IdempotencyKey;
import software.amazon.lambda.powertools.idempotency.Idempotent;
import software.amazon.lambda.powertools.idempotency.persistence.DynamoDBPersistenceStore;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.di.DaggerPaymentHandlerComponent;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.dto.PaymentRequest;
import software.amazon.lambda.samples.powertools.idempotency.sqs.payment.service.PaymentService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static software.amazon.lambda.powertools.utilities.EventDeserializer.extractDataFrom;

@AllArgsConstructor
public class PaymentHandler implements RequestHandler<SQSEvent, List<String>> {

    private static final Logger logger = LogManager.getLogger();

    @Inject
    PaymentService paymentService;

    public PaymentHandler() {
        DaggerPaymentHandlerComponent.builder().build().inject(this);
        Idempotency.config()
                .withPersistenceStore(
                        DynamoDBPersistenceStore.builder()
                                .withTableName(System.getenv("IDEMPOTENCY_TABLE"))
                                .build())
                .configure();
    }

    @Logging(logEvent = true)
    @Override
    public List<String> handleRequest(SQSEvent sqsEvent, Context context) {
        return sqsEvent.getRecords().stream().map(record -> process(record.getMessageId(), record.getBody())).collect(Collectors.toList());
    }

    @Idempotent
    private String process(String messageId, @IdempotencyKey String messageBody) {
        logger.info("Processing messageId: {}", messageId);
        PaymentRequest request = extractDataFrom(messageBody).as(PaymentRequest.class);
        return paymentService.process(request);
    }
}
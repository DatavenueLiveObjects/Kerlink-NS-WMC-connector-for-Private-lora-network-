/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

@Component
public class Counters {

    private final Counter messageReadCounter;
    private final Counter messageSentAttemptCounter;
    private final Counter messageSentAttemptFailedCounter;
    private final Counter messageSentCounter;
    private final Counter messageSentFailedCounter;

    public Counters(StepMeterRegistry registry) {
        messageReadCounter = registry.counter("message.read");
        messageSentAttemptCounter = registry.counter("message.sent.attempt");
        messageSentAttemptFailedCounter = registry.counter("message.sent.attempt.failed");
        messageSentCounter = registry.counter("message.sent");
        messageSentFailedCounter = registry.counter("message.sent.failed");
        registry.start(Executors.defaultThreadFactory());
    }

    public Counter getMessageReadCounter() {
        return messageReadCounter;
    }

    public Counter getMessageSentAttemptCounter() {
        return messageSentAttemptCounter;
    }

    public Counter getMessageSentAttemptFailedCounter() {
        return messageSentAttemptFailedCounter;
    }

    public Counter getMessageSentCounter() {
        return messageSentCounter;
    }

    public Counter getMessageSentFailedCounter() {
        return messageSentFailedCounter;
    }

    public List<Counter> getAll() {
        return Arrays.asList(messageReadCounter, messageSentAttemptCounter, messageSentAttemptFailedCounter, messageSentCounter, messageSentFailedCounter);
    }
}
/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo;

import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sample.kerlink2lo.lo.LoApiExternalConnectorService;
import com.orange.lo.sample.kerlink2lo.utils.Counters;
import io.micrometer.core.instrument.Counter;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@RestController
public class Kerlink2LoController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String KERLINK_ACCOUNT_HEADER = "Kerlink-Account";

    private final LoApiExternalConnectorService externalConnectorService;
    private final Counters counters;

    public Kerlink2LoController(LoApiExternalConnectorService externalConnectorService, Counters counters) {
        this.externalConnectorService = externalConnectorService;
        this.counters = counters;
    }

    @PostMapping("/dataUp")
    public Callable<ResponseEntity<Void>> dataUp(@RequestBody DataUpDto dataUpDto, @RequestHeader HttpHeaders headers) {
        Optional<String> kerlinkAccountName = getKerlinkAccountName(headers);
        incrementReadAndAttemptsCounters();

        if (LOG.isDebugEnabled()) {
            LOG.debug("KerlinkAccountName {}", StringEscapeUtils.escapeHtml4(kerlinkAccountName.orElse("")));
            LOG.debug("received {}", StringEscapeUtils.escapeHtml4(dataUpDto.toString()));
        }

        return () -> {
            if (kerlinkAccountName.isPresent()) {
                externalConnectorService.sendMessage(dataUpDto, kerlinkAccountName.get());
                return ResponseEntity.ok().build();
            } else {
                LOG.debug("Unknown KerlinkAccountName {}", StringEscapeUtils.escapeHtml4(kerlinkAccountName.orElse("")));
                incrementFailureCounters();

                return ResponseEntity.notFound().build();
            }
        };
    }

    @PostMapping("/dataDownEvent")
    public Callable<ResponseEntity<Void>> dataDown(@RequestBody DataDownEventDto dataDownEventDto, @RequestHeader HttpHeaders headers) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("received command response {}", StringEscapeUtils.escapeJava(dataDownEventDto.toString()));
        }
        return () -> {
            if ("OK".equals(dataDownEventDto.getStatus())) {
                externalConnectorService.sendCommandResponse(dataDownEventDto);
            }
            return ResponseEntity.ok().build();
        };
    }

    private void incrementReadAndAttemptsCounters() {
        Counter messageSentAttemptCounter = counters.getMessageSentAttemptCounter();
        messageSentAttemptCounter.increment();
        Counter messageReadCounter = counters.getMessageReadCounter();
        messageReadCounter.increment();
    }

    private void incrementFailureCounters() {
        Counter messageSentAttemptFailedCounter = counters.getMessageSentAttemptFailedCounter();
        messageSentAttemptFailedCounter.increment();
        Counter messageSentFailedCounter = counters.getMessageSentFailedCounter();
        messageSentFailedCounter.increment();
    }

    private Optional<String> getKerlinkAccountName(HttpHeaders headers) {

        List<String> strings = headers.get(KERLINK_ACCOUNT_HEADER);
        String name = strings != null && !strings.isEmpty() ? strings.get(0) : null;
        String cleanName = StringEscapeUtils.escapeHtml4(name);
        return Optional.ofNullable(cleanName);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleIOException(HttpClientErrorException ex, HttpServletRequest request) {
        LOG.error("Error while processing call {}, \n{}, \n {}", request.getRequestURI(), ex.getResponseBodyAsString(), ex.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getResponseBodyAsString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex, HttpServletRequest request) {
        LOG.error("Error while processing call {}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

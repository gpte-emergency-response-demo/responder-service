package com.redhat.cajun.navy.responder.listener;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.redhat.cajun.navy.responder.message.Message;
import com.redhat.cajun.navy.responder.message.ResponderUpdatedEvent;
import com.redhat.cajun.navy.responder.message.UpdateResponderCommand;
import com.redhat.cajun.navy.responder.model.Responder;
import com.redhat.cajun.navy.responder.service.ResponderService;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class ResponderCommandMessageListener {

    private final static Logger log = LoggerFactory.getLogger(ResponderCommandMessageListener.class);

    private static final String UPDATE_RESPONDER_COMMAND = "UpdateResponderCommand";
    private static final String[] ACCEPTED_MESSAGE_TYPES = {UPDATE_RESPONDER_COMMAND};

    @Autowired
    private ResponderService responderService;

    @Autowired
    private KafkaTemplate<String, Message<?>> kafkaTemplate;

    @Value("${sender.destination.reporter-updated-event}")
    private String destination;

    @KafkaListener(topics = "${listener.destination.update-responder-command}")
    public void processMessage(@Payload String messageAsJson) {

        acceptMessageType(messageAsJson).ifPresent(m -> {
            processUpdateResponderCommand(messageAsJson);
        });
    }

    private void processUpdateResponderCommand(String messageAsJson) {

        Message<UpdateResponderCommand> message;
        try {
            message = new ObjectMapper().readValue(messageAsJson, new TypeReference<Message<UpdateResponderCommand>>() {});
            Responder responder = message.getBody().getResponder();

            log.debug("Processing '" + UPDATE_RESPONDER_COMMAND + "' message for responder '" + responder.getId() + "'");

            Triple<Boolean, String, Responder> result = responderService.updateResponder(responder);
            String status = (result.getLeft() ? "success" : "error");
            ResponderUpdatedEvent event = new ResponderUpdatedEvent.Builder(status, result.getRight())
                    .statusMessage(result.getMiddle()).build();
            Message eventMessage = new Message.Builder<>("ResponderUpdatedEvent",
                    "ResponderService", event)
                    .header("incidentId", message.getHeaderValue("incidentId"))
                    .build();

            ListenableFuture<SendResult<String, Message<?>>> future = kafkaTemplate.send(destination, responder.getId(), eventMessage);
            future.addCallback(
                    res -> log.debug("Sent 'ResponderUpdatedEvent' message for responder " + responder.getId()),
                    ex -> log.error("Error sending 'IncidentReportedEvent' message for incident " + responder.getId(), ex));


        } catch (Exception e) {
            log.error("Error processing msg " + messageAsJson, e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Optional<String> acceptMessageType(String messageAsJson) {
        try {
            String messageType = JsonPath.read(messageAsJson, "$.messageType");
            if (Arrays.asList(ACCEPTED_MESSAGE_TYPES).contains(messageType)) {
                return Optional.of(messageType);
            }
            log.debug("Message with type '" + messageType + "' is ignored");
        } catch (Exception e) {
            log.warn("Unexpected message without 'messageType' field.");
        }
        return Optional.empty();
    }

}

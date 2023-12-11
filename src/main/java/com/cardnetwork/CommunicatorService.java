package com.cardnetwork;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CommunicatorService {
    public ObjectNode authenticateBank(ObjectNode objectNode){
        WebClient webClient = WebClient.create();
        objectNode = webClient.post()
                .uri("http://BankA:8080/payment")
                .bodyValue(objectNode)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        return objectNode;
    }
    public ObjectNode authorizeBank(ObjectNode objectNode){
        WebClient webClient = WebClient.create();
        objectNode = webClient.post()
                .uri("http://BankA:8080/authorize")
                .bodyValue(objectNode)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        return objectNode;
    }
}

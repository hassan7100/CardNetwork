package com.cardnetwork;

import com.cardnetwork.utility.CsvWriterReader;
import com.cardnetwork.utility.RSAKeyGenerator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CommunicatorService {
    public ObjectNode authenticateBank(ObjectNode cardRequest) throws Exception {
        WebClient webClient = WebClient.create();
        cardRequest = webClient.post()
                .uri("http://BankA:8080/payment")
                .bodyValue(cardRequest)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        return cardRequest;
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

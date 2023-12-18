package com.cardnetwork;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CommunicatorService {
    public ObjectNode authenticateBank(ObjectNode cardRequest, String bank) throws Exception {
        WebClient webClient = WebClient.create();
        return webClient.post()
                .uri("http://"+bank+":8080/payment/authenticate")
                .bodyValue(cardRequest)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }
    public ObjectNode forwardSubDeposit(ObjectNode depositReq, String bank) throws Exception {
        WebClient webClient = WebClient.create();
        return webClient.post()
                .uri("http://"+bank+":8080/payment/deposit/sub")
                .bodyValue(depositReq)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }
    public ObjectNode authorizeBank(ObjectNode objectNode, String bank){
        WebClient webClient = WebClient.create();
        objectNode = webClient.post()
                .uri("http://"+bank+":8080/payment/authorize")
                .bodyValue(objectNode)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        return objectNode;
    }
}

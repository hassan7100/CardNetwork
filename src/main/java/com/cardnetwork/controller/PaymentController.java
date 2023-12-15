package com.cardnetwork.controller;

import com.cardnetwork.Service.PaymentService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping("/payment")
    public ObjectNode payment(@RequestBody ObjectNode cardRequest) throws Exception {
        return paymentService.authenticateBank(cardRequest);
    }
    @PostMapping("/authorize")
    public ObjectNode authorize(@RequestBody ObjectNode objectNode) throws Exception {
        ObjectNode objectNode1 = paymentService.authorizationBank(objectNode);
        System.out.println(objectNode1);
        return objectNode1;
    }
}

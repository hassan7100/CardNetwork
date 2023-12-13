package com.cardnetwork.controller;

import com.cardnetwork.AtomicID;
import com.cardnetwork.CommunicatorService;
import com.cardnetwork.utility.CsvWriterReader;
import com.cardnetwork.utility.RSAKeyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    RSAKeyGenerator rsaKeyGenerator;
    @Autowired
    CsvWriterReader csvWriterReader;
    @Autowired
    CommunicatorService communicatorService;
    @Autowired
    AtomicID atomicID;
    @PostMapping("/payment")
    public ObjectNode payment(@RequestBody ObjectNode cardRequest) throws Exception {
        ObjectNode objectNode = rsaKeyGenerator.decryptObjectNode(cardRequest.get("object").asText(),csvWriterReader.readMyPrivate());
        if (objectNode.get("ID").asInt() != atomicID.getID()){
            objectNode.put("status","Invalid");
        }
        String encrypted = rsaKeyGenerator.encryptObjectNode(objectNode,csvWriterReader.readPublic("BankA"));
        ObjectNode objectNode1 = communicatorService.authenticateBank(new ObjectMapper().createObjectNode().put("object",encrypted));
        objectNode = rsaKeyGenerator.decryptObjectNode(objectNode1.get("object").asText(),csvWriterReader.readMyPrivate());
        encrypted = rsaKeyGenerator.encryptObjectNode(objectNode,csvWriterReader.readPublic("gateway"));
        return new ObjectMapper().createObjectNode().put("object",encrypted);
    }
    @PostMapping("/authorize")
    public ObjectNode authorize(@RequestBody ObjectNode objectNode) throws Exception {
        ObjectNode objectNode1 = rsaKeyGenerator.decryptObjectNode(objectNode.get("object").asText(),csvWriterReader.readMyPrivate());
        String encrypted = rsaKeyGenerator.encryptObjectNode(objectNode1,csvWriterReader.readPublic("BankA"));
        objectNode1 = communicatorService.authorizeBank(new ObjectMapper().createObjectNode().put("object",encrypted));
        objectNode = rsaKeyGenerator.decryptObjectNode(objectNode1.get("object").asText(),csvWriterReader.readMyPrivate());
        encrypted = rsaKeyGenerator.encryptObjectNode(objectNode,csvWriterReader.readPublic("gateway"));
        return new ObjectMapper().createObjectNode().put("object",encrypted);
    }
}

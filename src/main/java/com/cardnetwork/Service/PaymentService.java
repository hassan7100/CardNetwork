package com.cardnetwork.Service;


import com.cardnetwork.AtomicID;
import com.cardnetwork.CommunicatorService;
import com.cardnetwork.utility.CsvWriterReader;
import com.cardnetwork.utility.RSAKeyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    private RSAKeyGenerator rsaKeyGenerator;
    @Autowired
    private CsvWriterReader csvWriterReader;
    @Autowired
    private CommunicatorService communicatorService;
    @Autowired
    private AtomicID atomicID;
    public ObjectNode authenticateBank(ObjectNode cardRequest) throws Exception {
        ObjectNode authenticationRequest = rsaKeyGenerator.decryptObjectNode(cardRequest.get("object").asText(),csvWriterReader.readMyPrivate());
        if (authenticationRequest.get("ID").asInt() != atomicID.getID()){
            authenticationRequest.put("status","Invalid");
        }
        String PAN = authenticationRequest.get("PAN").asText();
        if (PAN.startsWith("1100")){
            String encrypted = rsaKeyGenerator.encryptObjectNode(authenticationRequest,csvWriterReader.readPublic("BankA"));
            ObjectNode authenticationResponse = communicatorService.authenticateBank(new ObjectMapper().createObjectNode().put("object",encrypted), "BankA");
            authenticationResponse = rsaKeyGenerator.decryptObjectNode(authenticationResponse.get("object").asText(),csvWriterReader.readMyPrivate());
            encrypted = rsaKeyGenerator.encryptObjectNode(authenticationResponse, csvWriterReader.readPublic("gateway"));
            return new ObjectMapper().createObjectNode().put("object",encrypted);
        }
        else if (PAN.startsWith("1133")){
            String encrypted = rsaKeyGenerator.encryptObjectNode(authenticationRequest,csvWriterReader.readPublic("BankB"));
            ObjectNode authenticationResponse = communicatorService.authenticateBank(new ObjectMapper().createObjectNode().put("object",encrypted), "BankB");
            authenticationResponse = rsaKeyGenerator.decryptObjectNode(authenticationResponse.get("object").asText(),csvWriterReader.readMyPrivate());
            encrypted = rsaKeyGenerator.encryptObjectNode(authenticationResponse, csvWriterReader.readPublic("gateway"));
            return new ObjectMapper().createObjectNode().put("object",encrypted);
        }
        else {
            authenticationRequest.put("status", "Invalid");
            String encrypted = rsaKeyGenerator.encryptObjectNode(authenticationRequest, csvWriterReader.readPublic("gateway"));
            return new ObjectMapper().createObjectNode().put("object", encrypted);
        }
    }
    public ObjectNode authorizationBank(ObjectNode objectNode) throws Exception {
        ObjectNode authorizationRequest = rsaKeyGenerator.decryptObjectNode(objectNode.get("object").asText(),csvWriterReader.readMyPrivate());
        String PAN = authorizationRequest.get("PAN").asText();
        if (PAN.startsWith("1100")){
            String encrypted = rsaKeyGenerator.encryptObjectNode(authorizationRequest,csvWriterReader.readPublic("BankA"));
            ObjectNode authorizationResponse = communicatorService.authorizeBank(new ObjectMapper().createObjectNode().put("object",encrypted), "BankA");
            authorizationResponse = rsaKeyGenerator.decryptObjectNode(authorizationResponse.get("object").asText(),csvWriterReader.readMyPrivate());
            encrypted = rsaKeyGenerator.encryptObjectNode(authorizationResponse,csvWriterReader.readPublic("gateway"));
            return new ObjectMapper().createObjectNode().put("object",encrypted);
        }
        else if (PAN.startsWith("1133")){
            String encrypted = rsaKeyGenerator.encryptObjectNode(authorizationRequest,csvWriterReader.readPublic("BankB"));
            ObjectNode authorizationResponse = communicatorService.authorizeBank(new ObjectMapper().createObjectNode().put("object",encrypted), "BankB");
            authorizationResponse = rsaKeyGenerator.decryptObjectNode(authorizationResponse.get("object").asText(),csvWriterReader.readMyPrivate());
            encrypted = rsaKeyGenerator.encryptObjectNode(authorizationResponse,csvWriterReader.readPublic("gateway"));
            return new ObjectMapper().createObjectNode().put("object",encrypted);
        }
        else {
            authorizationRequest.put("status", "Invalid");
            String encrypted = rsaKeyGenerator.encryptObjectNode(authorizationRequest, csvWriterReader.readPublic("gateway"));
            return new ObjectMapper().createObjectNode().put("object", encrypted);
        }

    }
}

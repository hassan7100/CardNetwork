package com.cardnetwork.Service;


import com.cardnetwork.AtomicID;
import com.cardnetwork.CommunicatorService;
import com.cardnetwork.utility.CsvWriterReader;
import com.cardnetwork.utility.RSAKeyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final RSAKeyGenerator rsaKeyGenerator;
    private final CsvWriterReader csvWriterReader;
    private final CommunicatorService communicatorService;
    private final AtomicID atomicID;
    @Value("${bankA.id}")
    private String BANK_A_ID;
    @Value("${bankB.id}")
    private String BANK_B_ID;
    @Value("${bankA.name}")
    private String BANK_A_NAME;
    @Value("${bankB.name}")
    private String BANK_B_NAME;

    @Autowired
    public PaymentService(RSAKeyGenerator rsaKeyGenerator,
                          CsvWriterReader csvWriterReader,
                          CommunicatorService communicatorService,
                          AtomicID atomicID) {
        this.rsaKeyGenerator = rsaKeyGenerator;
        this.csvWriterReader = csvWriterReader;
        this.communicatorService = communicatorService;
        this.atomicID = atomicID;
    }


    public ObjectNode authenticateBank(ObjectNode cardRequest) throws Exception {
        ObjectNode authenticationRequest = rsaKeyGenerator.decryptObjectNode(cardRequest, csvWriterReader.readMyPrivate());
        if (authenticationRequest.get("ID").asInt() != atomicID.getID()) {
            authenticationRequest.put("status", "Invalid");
        }
        String PAN = authenticationRequest.get("PAN").asText();
        if (PAN.startsWith(BANK_A_ID)) {
            return sendAuthenticationRequest(authenticationRequest,BANK_A_NAME);
        } else if (PAN.startsWith(BANK_B_ID)) {
            return sendAuthenticationRequest(authenticationRequest,BANK_B_NAME);
        }
        authenticationRequest.put("status", "Invalid");
        return rsaKeyGenerator.encryptObjectNode(authenticationRequest, csvWriterReader.readPublic("gateway"));
    }

    private ObjectNode sendAuthenticationRequest(ObjectNode authenticationRequest, String bankName) throws Exception {
        ObjectNode encrypted = rsaKeyGenerator.encryptObjectNode(authenticationRequest, csvWriterReader.readPublic(bankName));
        ObjectNode authenticationResponse = communicatorService.authenticateBank(encrypted, bankName);
        authenticationResponse = rsaKeyGenerator.decryptObjectNode(authenticationResponse, csvWriterReader.readMyPrivate());
        return rsaKeyGenerator.encryptObjectNode(authenticationResponse, csvWriterReader.readPublic("gateway"));
    }

    public ObjectNode authorizationBank(ObjectNode objectNode) throws Exception {

        ObjectNode authorizationRequest = rsaKeyGenerator.decryptObjectNode(objectNode, csvWriterReader.readMyPrivate());
        String PAN = authorizationRequest.get("PAN").asText();
        if (PAN.startsWith(BANK_A_ID)) {
            return sendAuthorizationRequest(authorizationRequest,BANK_A_NAME);
        } else if (PAN.startsWith(BANK_B_ID)) {
            return sendAuthorizationRequest(authorizationRequest,BANK_B_NAME);
        }
        authorizationRequest.put("status", "Invalid");
        return rsaKeyGenerator.encryptObjectNode(authorizationRequest, csvWriterReader.readPublic("gateway"));
    }

    private ObjectNode sendAuthorizationRequest(ObjectNode authorizationRequest, String bankName) throws Exception {
        ObjectNode encrypted = rsaKeyGenerator.encryptObjectNode(authorizationRequest, csvWriterReader.readPublic(bankName));
        ObjectNode authorizationResponse = communicatorService.authorizeBank(encrypted, bankName);
        authorizationResponse = rsaKeyGenerator.decryptObjectNode(authorizationResponse, csvWriterReader.readMyPrivate());
        return rsaKeyGenerator.encryptObjectNode(authorizationResponse, csvWriterReader.readPublic("gateway"));
    }

    public ObjectNode redirectDeposit(ObjectNode request) throws Exception {
        ObjectNode decryptedReq = rsaKeyGenerator.decryptObjectNode(request, csvWriterReader.readMyPrivate());
        String bankName = decryptedReq.get("bankName").asText();
        String subAccountID = decryptedReq.get("subAccountID").asText();
        Double amount = decryptedReq.get("amount").asDouble();
        ObjectNode redirectReq = new ObjectMapper().createObjectNode();
        redirectReq.put("subAccountID", subAccountID);
        redirectReq.put("amount", amount);
        if (bankName.equals(BANK_A_NAME)) {
            return getJsonNodes(redirectReq, BANK_B_NAME, BANK_A_NAME);
        } else if (bankName.equals(BANK_B_NAME)) {
            return getJsonNodes(redirectReq, BANK_A_NAME, BANK_B_NAME);
        }
        return null;
    }

    private ObjectNode getJsonNodes(ObjectNode redirectReq, String bankBName, String bankAName) throws Exception {
        ObjectNode encryptedRedirectReq = rsaKeyGenerator.encryptObjectNode(redirectReq, csvWriterReader.readPublic(bankBName));
        ObjectNode bankBResponse = communicatorService.forwardSubDeposit(encryptedRedirectReq, bankBName);
        bankBResponse = rsaKeyGenerator.decryptObjectNode(bankBResponse, csvWriterReader.readMyPrivate());
        return rsaKeyGenerator.encryptObjectNode(bankBResponse,csvWriterReader.readPublic(bankAName));
    }
}

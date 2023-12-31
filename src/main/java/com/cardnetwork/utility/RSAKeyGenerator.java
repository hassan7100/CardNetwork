package com.cardnetwork.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

@Service
public class RSAKeyGenerator {
    private static KeyPair keyPair;
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        if (keyPair == null) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        return keyPair;
    }

    public ObjectNode encryptObjectNode(ObjectNode objectNode, PublicKey publicKey) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(objectNode);


        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(jsonString.getBytes());

        String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
        return objectMapper.createObjectNode().put("object",encrypted);
    }

    public ObjectNode decryptObjectNode(ObjectNode encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String encrypted = encryptedData.get("object").asText();
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        ObjectMapper objectMapper = new ObjectMapper();
        return (ObjectNode) objectMapper.readTree(new String(decryptedBytes));
    }




}
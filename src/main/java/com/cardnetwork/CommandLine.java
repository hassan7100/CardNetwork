package com.cardnetwork;

import com.cardnetwork.utility.CsvWriterReader;
import com.cardnetwork.utility.RSAKeyGenerator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.KeyPair;

@Component
public class CommandLine implements CommandLineRunner{
    private final RSAKeyGenerator rsaKeyGenerator;
    private final CsvWriterReader csvWriterReader;
    @Value("${bankA.name}")
    private String BANK_A_NAME;
    @Value("${bankB.name}")
    private String BANK_B_NAME;

    @Autowired
    public CommandLine(RSAKeyGenerator rsaKeyGenerator, CsvWriterReader csvWriterReader) {
        this.rsaKeyGenerator = rsaKeyGenerator;
        this.csvWriterReader = csvWriterReader;
    }

    public void run(String... args) throws Exception {
        KeyPair keyPair = rsaKeyGenerator.generateKeyPair();
        csvWriterReader.writeKeys(keyPair);
        Thread.sleep(30000);
        WebClient webClient = WebClient.create();
        ObjectNode objectNode = webClient.get()
                .uri("http://gateway:8080/getPublic")
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        csvWriterReader.writePublicKey("gateway", objectNode.get("PublicKey").asText());
        objectNode = webClient.get()
                .uri("http://"+BANK_A_NAME+":8080/getPublic")
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        csvWriterReader.writePublicKey(BANK_A_NAME, objectNode.get("PublicKey").asText());
        objectNode = webClient.get()
                .uri("http://"+BANK_B_NAME+":8080/getPublic")
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        csvWriterReader.writePublicKey(BANK_B_NAME, objectNode.get("PublicKey").asText());
    }
}

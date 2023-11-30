package com.cardnetwork;

import com.cardnetwork.utility.CsvWriterReader;
import com.cardnetwork.utility.RSAKeyGenerator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.KeyPair;

@Component
public class CommandLine implements CommandLineRunner{
    @Autowired
    private RSAKeyGenerator rsaKeyGenerator;
    @Autowired
    private CsvWriterReader csvWriterReader;

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
    }
}

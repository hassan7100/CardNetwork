package com.cardnetwork.controller;

import com.cardnetwork.utility.CsvWriterReader;
import com.cardnetwork.utility.RSAKeyGenerator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class Payment {
    @Autowired
    RSAKeyGenerator rsaKeyGenerator;
    @Autowired
    CsvWriterReader csvWriterReader;
    @PostMapping("/payment")
    public ObjectNode payment(@RequestBody ObjectNode cardRequest) throws Exception {
        return rsaKeyGenerator.decryptObjectNode(cardRequest.get("object").asText(),csvWriterReader.readMyPrivate());
    }
}

package com.cardnetwork.utility;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class CsvWriterReader {
    private String filePath = "./Keys.csv";

    public void writeKeys(KeyPair keyPair) {
        try {
            // Check if the file exists
            if (!Files.exists(Paths.get(filePath))) {
                // If it doesn't exist, create the parent directories and the file
                System.out.println(new File(filePath).createNewFile());
            }
            try (FileWriter outputFile = new FileWriter(filePath)) {
                CSVWriter writer = new CSVWriter(outputFile);
                writer.writeNext(new String[]{Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()),
                        Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())});
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
        public void writePublicKey(String name,String publicKey) {
        try (FileWriter outputFile = new FileWriter(filePath,true)) {
            CSVWriter writer = new CSVWriter(outputFile);

            writer.writeNext(new String[]{name, publicKey});
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public PublicKey readMyPublic(){
        try(FileReader inputFile = new FileReader(filePath)) {
            CSVReader reader = new CSVReader(inputFile);
            byte[] publicKeyBytes = Base64.getDecoder().decode(reader.readNext()[0].trim().getBytes());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (IOException e) {
            return null;
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
    public PrivateKey readMyPrivate(){
        try(FileReader inputFile = new FileReader(filePath)) {
            CSVReader reader = new CSVReader(inputFile);
            byte[] publicKeyBytes = Base64.getDecoder().decode(reader.readNext()[1].trim().getBytes());
            PKCS8EncodedKeySpec publicKeySpec = new PKCS8EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(publicKeySpec);
        } catch (IOException e) {
            return null;
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
    public PublicKey readPublic(String name){
        String[] nextRecord;
        try (FileReader inputFile = new FileReader(filePath)) {
            CSVReader reader = new CSVReader(inputFile);
            while ((nextRecord = reader.readNext()) != null) {
                if(nextRecord[0].equals(name)){
                    byte[] publicKeyBytes = Base64.getDecoder().decode(nextRecord[1].getBytes());
                    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    return keyFactory.generatePublic(publicKeySpec);
                }
            }
        } catch (IOException ignored) {

        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

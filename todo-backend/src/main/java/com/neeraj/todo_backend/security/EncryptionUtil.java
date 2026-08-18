package com.neeraj.todo_backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private final SecretKeySpec secretKey;

    public EncryptionUtil(@Value("${app.encryption.key}") String key) {
        if (key == null || key.length() != 16 && key.length() != 24 && key.length() != 32) {
            throw new IllegalArgumentException("app.encryption.key must be 16, 24, or 32 characters long");
        }
        this.secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(
                    cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Error while encrypting data", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(
                    cipher.doFinal(Base64.getDecoder().decode(encryptedData)),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Error while decrypting data", e);
        }
    }
}

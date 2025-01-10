package com.back.sousa.models.database;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

    private static final String SECRET_KEY_B64 = System.getenv("ALGORITHM_SECRET_KEY");
    private static final String TRANSFORMATION = System.getenv("ENCRYPTION_ALGORITHM");

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return decrypt(dbData);
    }

    private String encrypt(String data) {
        try {
            // Descodifica de Base64 para bytes
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY_B64);
            // Usa "AES" como algoritmo base
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao encriptar dados", ex);
        }
    }

    private String decrypt(String data) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY_B64);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao desencriptar dados", ex);
        }
    }
}
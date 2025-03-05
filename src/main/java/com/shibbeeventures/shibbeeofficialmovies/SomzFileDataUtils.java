package com.shibbeeventures.shibbeeofficialmovies;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Senestro
 */
public class SomzFileDataUtils {

    private static final String TRANSFORMATION = "AES/CTR/NoPadding";
    private static final String ALGORITHM = "AES";

    public static byte[] encrypt(byte[] data, String key) {
        try {
            byte[] iv = new byte[16]; // 128-bit IV (CTR mode)
            new SecureRandom().nextBytes(iv); // Generate a random IV
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(iv); // Write the IV at the start of the byte array
            try (CipherOutputStream cos = new CipherOutputStream(baos, cipher)) {
                cos.write(data);
            }
            // Return encrypted byte array
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            // Return null if an error occurs
            return null;
        }
    }

    public static byte[] decrypt(byte[] encryptedData, String key) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(encryptedData);
            byte[] iv = new byte[16]; // 128-bit IV (CTR mode)
            bais.read(iv); // Read the IV from the beginning of the byte array
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (CipherInputStream cis = new CipherInputStream(bais, cipher)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
            }
            // Return decrypted byte array
            return baos.toByteArray();
        } catch (Exception e) {
            // Return null if an error occurs
            e.printStackTrace();
            return null;
        }
    }
}

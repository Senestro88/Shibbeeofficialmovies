package com.shibbeeventures.shibbeeofficialmovies.cipher;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.conscrypt.Conscrypt;

public class SomzFileChaCha20 {

    static {
        // Register Conscrypt
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
    }

    /**
     * Encrypts data using the ChaCha20 algorithm.
     * 
     * @param data  The plaintext data
     * @param key   A 32 bytes key
     * @param nonce A 12 bytes nonce
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] nonce) throws Exception {
        Cipher cipher = getChaCha20Cipher();
        SecretKeySpec keySpec = new SecretKeySpec(key, "ChaCha20");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(data);
    }

    /**
     * Encrypts data using the ChaCha20 algorithm.
     * 
     * @param data    The plaintext data
     * @param key     A 32 bytes key
     * @param nonce   A 12 bytes nonce
     * @param counter The counter
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] nonce, int counter) throws Exception {
        Cipher cipher = getChaCha20Cipher();
        ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonce, counter);
        SecretKeySpec keySpec = new SecretKeySpec(key, "ChaCha20");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
        return cipher.doFinal(data);
    }

    /**
     * Decrypts encrypted data using the ChaCha20 algorithm.
     * 
     * @param encryptedData The encrypted data
     * @param key           A 32 bytes key
     * @param nonce         A 12 bytes nonce
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] nonce) throws Exception {
        Cipher cipher = getChaCha20Cipher();
        SecretKeySpec keySpec = new SecretKeySpec(key, "ChaCha20");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(encryptedData);
    }

    /**
     * Decrypts encrypted data using the ChaCha20 algorithm.
     * 
     * @param encryptedData The encrypted data
     * @param key           A 32 bytes key
     * @param nonce         A 12 bytes nonce
     * @param counter       The counter
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] nonce, int counter) throws Exception {
        Cipher cipher = getChaCha20Cipher();
        ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonce, counter);
        SecretKeySpec keySpec = new SecretKeySpec(key, "ChaCha20");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
        return cipher.doFinal(encryptedData);
    }

    // ================ PRIVATE METHODS ================ //

    private static Cipher getChaCha20Cipher() throws Exception {
        try {
            return Cipher.getInstance("ChaCha20", "Conscrypt");
        } catch (Exception exception) {
            throw new RuntimeException("ChaCha20 is not supported", exception);
        }
    }
}

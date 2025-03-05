package com.shibbeeventures.shibbeeofficialmovies.cipher;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SomzFileAesCtr {

    /**
     * Encrypts data using the AES-CTR algorithm.
     * 
     * @param data The plaintext data
     * @param key  A 32 bytes key
     * @param iv   A 16 bytes initialization vector
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(data);
    }

    /**
     * Decrypts encrypted data using the AES-CTR algorithm.
     * 
     * @param encryptedData The encrypted data
     * @param key           A 32 bytes key
     * @param iv            A 16 bytes initialization vector
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(encryptedData);
    }

}

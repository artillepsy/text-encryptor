package com.artillepsy.textencryptor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Provides AES/GCM encryption and decryption utilities using password-derived keys.
 */
public class EncryptionHelper {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 128;
    private static final byte[] FIXED_SALT = "StaticSaltForLocalApp".getBytes();

    /**
     * Derives a 256-bit AES key from password using PBKDF2WithHmacSHA256.
     *
     * @param password the password to derive the key from
     * @return SecretKey suitable for AES encryption
     * @throws Exception if key generation fails
     */
    public static SecretKey createKey(String password) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }


    /**
     * Encrypts plain text using AES/GCM with random IV, returns Base64 encoded result.
     *
     * @param plainText the text to encrypt
     * @param key       the AES secret key generated from a password
     * @return Base64 encoded string containing the IV and encrypted data
     * @throws Exception if encryption fails
     */
    public static String encrypt(String plainText, SecretKey key) throws Exception {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        Cipher cipher =  Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE,  key, new GCMParameterSpec(TAG_LENGTH, iv));
        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        ByteBuffer bb = ByteBuffer.allocate(iv.length + cipherText.length);
        bb.put(iv).put(cipherText);
        return Base64.getEncoder().withoutPadding().encodeToString(bb.array());
    }


    /**
     * Decrypts Base64 encoded data using AES/GCM, extracting IV from first 12 bytes.
     *
     * @param encryptedData Base64 encoded string containing the IV and encrypted data
     * @param key           the AES secret key generated from a password
     * @return the decrypted plain text
     * @throws Exception if decryption fails
     */
    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, decoded, 0, 12));
        return new String(cipher.doFinal(decoded, 12, decoded.length - 12));
    }
}


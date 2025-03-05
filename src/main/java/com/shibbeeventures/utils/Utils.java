package com.shibbeeventures.utils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.shibbeeventures.annotations.NonNull;
import com.shibbeeventures.annotations.Nullable;

public class Utils {
    private static final int GET_FILE_BYTES_CHUNK_SIZE = 1048576;

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean notNull(Object object) {
        return object != null;
    }

    public static boolean nonNull(Object object) {
        return notNull(object);
    }

    public static String string2hex(@NonNull String input) {
        return bytes2hex(input.getBytes());
    }

    public static String byte2hex(byte b) {
        return String.format("%02X", b);
    }

    public static String bytes2hex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }

    public static String string2hexPairs(@NonNull String input) {
        byte[] bytes = input.getBytes();
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X ", b));
        }
        return hex.toString().trim();
    }

    public static String bytes2hexPairs(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X ", b));
        }
        return hex.toString().trim();
    }

    public static byte[] hex2bytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String shuffle(@NonNull String str) {
        char[] chars = str.toCharArray();
        Random rand = new Random();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1); // Random index
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    public static String reverse(@NonNull String str) {
        char[] chars = str.toCharArray();
        int left = 0, right = chars.length - 1;
        while (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }
        return new String(chars);
    }

    public static String md5(@NonNull String input) {
        try {
            // Create MD5 MessageDigest instance
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Convert input string to bytes and update the digest, force UTF-8 encoding (same as PHP)
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            // Convert bytes to hexadecimal format
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b)); // Convert each byte to a 2-digit hex
            }
            return hexString.toString(); // Return the final hash
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static String extractNumbers(String input) {
        return input.replaceAll("\\D", ""); // Remove all non-digit characters
    }

    public static String extractLetters(String input) {
        return input.replaceAll("[^a-zA-Z]", ""); // Remove all non-letter characters
    }

    public static byte[] getFileBytes(@NonNull File file) throws IOException {
        return getFileBytes(file.getAbsolutePath());
    }

    /**
     * Reads a file in chunks and returns the full byte array.
     *
     * @param filePath The path to the file.
     * @return A byte array containing the entire file's data.
     * @throws IOException If an I/O error occurs.
     */
    public static byte[] getFileBytes(@NonNull String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("File is too large to be stored in a single byte array.");
        } else {
            byte[] fileBytes = new byte[(int) fileSize];
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                int bytesRead, offset = 0;
                byte[] buffer = new byte[GET_FILE_BYTES_CHUNK_SIZE];
                while ((bytesRead = bis.read(buffer)) != -1) {
                    System.arraycopy(buffer, 0, fileBytes, offset, bytesRead);
                    offset += bytesRead;
                }
            }
            return fileBytes;
        }
    }

    /**
     * Reads a file in chunks and returns a list of byte arrays.
     *
     * @param filePath The path to the file.
     * @return A list of byte arrays, where each array is a chunk of the file.
     * @throws IOException If an I/O error occurs.
     */
    public static List<byte[]> getFileBytesAsList(@NonNull String filePath) throws IOException {
        File file = new File(filePath);
        List<byte[]> chunks = new ArrayList<>();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[GET_FILE_BYTES_CHUNK_SIZE];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                // If the last chunk is smaller, resize the buffer
                if (bytesRead < GET_FILE_BYTES_CHUNK_SIZE) {
                    byte[] lastChunk = new byte[bytesRead];
                    System.arraycopy(buffer, 0, lastChunk, 0, bytesRead);
                    chunks.add(lastChunk);
                } else {
                    chunks.add(buffer.clone());
                }
            }
        }
        return chunks;
    }

    public static void saveBytesToFile(@NonNull byte[] data, @NonNull String filename) throws IOException {
        File file = new File(filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
        }
    }

    // Convert a single byte to an unsigned byte stored as an int (0-255)
    public static int getUnsignedByte(byte b) {
        return b & 0xFF; // Convert to unsigned
    }

    // Convert a byte array to an array of unsigned bytes stored in int[]
    public static int[] getUnsignedBytes(byte[] bytes) {
        int[] result = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i] & 0xFF; // Convert to unsigned
        }
        return result;
    }

    public static void closeQuietly(@Nullable Closeable closeable) {
        if (notNull(closeable)) {
            try {
                closeable.close();
            } catch (Exception exception) {
            }
        }
    }
}

package com.shibbeeventures.shibbeeofficialmovies.cipher.header;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shibbeeventures.annotations.NonNull;
import com.shibbeeventures.annotations.Nullable;
import com.shibbeeventures.shibbeeofficialmovies.cipher.SomzFileAesCtr;
import com.shibbeeventures.utils.Utils;

// USING A RADOM POSITION

public class SomzFileHeaderCipherBuffer {
    public static final int sizeOfHeader = 200;

    public static byte[] encrypt(byte[] data, int position, int keysize,
            @Nullable ProgressHandler progressHandler) throws IOException {
        return encDecProcess(data, position, keysize, progressHandler);
    }

    public static byte[] decrypt(byte[] data, int position, int keysize,
            @Nullable ProgressHandler progressHandler) throws IOException {
        return encDecProcess(data, position, keysize, progressHandler);
    }

    public static HashMap<String, String> readHeader(@NonNull InputStream inputStream, @NonNull String encKey)
            throws IOException {
        HashMap<String, String> map = new HashMap<>();
        byte[] buffer = new byte[sizeOfHeader];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead > 0) {
            byte[] ivBytes = Utils.reverse(new String(encKey)).substring(0, 16).getBytes();
            try {
                byte[] decHeader = SomzFileAesCtr.decrypt(buffer, encKey.getBytes(), ivBytes);
                String actualHeader = trimNullPadding(new String(decHeader));
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> hashMap = gson.fromJson(actualHeader, type);
                if (hashMap.containsKey("position") && hashMap.containsKey("version") && hashMap.containsKey("time")
                        && hashMap.containsKey("keysize")) {
                    map = hashMap;
                }
            } catch (Exception exception) {
                throw new IOException(exception);
            }
        }
        return map;
    }

    public static String trimNullPadding(@NonNull String input) {
        return input.replaceAll("\0+$", ""); // Remove trailing null characters
    }

    public static int getSizeOfHeader() {
        return sizeOfHeader;
    }

    // ===================== PRIVATE METHODS ===================== //

    private static byte[] encDecProcess(byte[] data, int position, int keysize,
            @Nullable ProgressHandler progressListener) {
        byte[] keysizeBytes = keysizeToBytes(keysize);
        byte[] output = new byte[data.length];
        int sizeOfKey = keysizeBytes.length;
        int sizeOfData = data.length;
        for (int i = 0; i < sizeOfData; i++) {
            int pos = position % sizeOfKey;
            byte keyByte = keysizeBytes[pos];
            output[i] = (byte) ((data[i] ^ keyByte) & 0xFF); // XOR-based swapping using only the key length
            if (Utils.notNull(progressListener) && sizeOfData > 0) {
                double progress = ((double) (i + 1) / sizeOfData) * 100;
                progressListener.onProgress(String.format("%.2f", progress));
            }
        }
        return output;
    }

    private static byte[] keysizeToBytes(int keysize) {
        return new String(String.valueOf(keysize)).getBytes();
    }

    // ===================== PUBLIC ENUM ===================== //
    public interface ProgressHandler {
        void onProgress(@NonNull String percentage);
    }
}

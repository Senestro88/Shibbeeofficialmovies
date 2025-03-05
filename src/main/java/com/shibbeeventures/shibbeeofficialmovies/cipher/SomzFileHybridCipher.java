package com.shibbeeventures.shibbeeofficialmovies.cipher;

import java.io.*;
import java.util.Arrays;

import com.shibbeeventures.annotations.NonNull;
import com.shibbeeventures.annotations.Nullable;
import com.shibbeeventures.utils.Utils;

// USING A FIXED POSITION

public class SomzFileHybridCipher {
    private final byte[] encKey;
    private static final int sizeOfFileChunk = 1048576; // Process 1MB at a time

    public SomzFileHybridCipher(@NonNull String encKey) {
        this.encKey = encKey.getBytes();
    }

    public byte[] encrypt(byte[] data, @Nullable ProgressListener progressListener) {
        return this.encDecProcess(data, progressListener);
    }

    public byte[] decrypt(byte[] data, @Nullable ProgressListener progressListener) {
        return this.encDecProcess(data, progressListener);
    }

    public void encryptFile(@NonNull String inputFile, @NonNull String outputFile,
            @Nullable ProgressListener progressListener) throws IOException {
        this.encDecFileProcess(inputFile, outputFile, progressListener);
    }

    public void decryptFile(@NonNull String inputFile, @NonNull String outputFile,
            @Nullable ProgressListener progressListener) throws IOException {
        this.encDecFileProcess(inputFile, outputFile, progressListener);
    }

    // ===================== PRIVATE METHODS ===================== //

    private byte[] encDecProcess(byte[] data, @Nullable ProgressListener progressListener) {
        byte[] output = new byte[data.length];
        int sizeOfKey = encKey.length;
        int sizeOfData = data.length;
        for (int i = 0; i < sizeOfData; i++) {
            int pos = 1 % sizeOfKey;
            byte keyByte = encKey[pos];
            output[i] = (byte) ((data[i] ^ keyByte) & 0xFF); // XOR-based swapping using only the key length
            if (Utils.notNull(progressListener) && sizeOfData > 0) {
                double progress = ((double) (i + 1) / sizeOfData) * 100;
                progressListener.onProgress(String.format("%.2f", progress));
            }
        }
        return output;
    }

    private void encDecFileProcess(@NonNull String inputFile, @NonNull String outputFile,
            @Nullable ProgressListener progressListener) throws IOException {
        File file = new File(inputFile);
        long sizeOfFile = file.length();
        long sizeOfProcessedFile = 0;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile),
                sizeOfFileChunk);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile),
                        sizeOfFileChunk)) {
            byte[] buffer = new byte[sizeOfFileChunk];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                for (int i = 0; i < chunk.length; i++) {
                    int pos = ((int) sizeOfProcessedFile + 1) % encKey.length;
                    byte keyByte = encKey[pos];
                    chunk[i] = (byte) ((chunk[i] ^ keyByte) & 0xFF); // XOR-based swapping using only the key length
                }
                out.write(chunk);
                sizeOfProcessedFile += bytesRead;
                if (Utils.notNull(progressListener) && sizeOfFile > 0) {
                    double progress = ((double) sizeOfProcessedFile / sizeOfFile) * 100;
                    progressListener.onProgress(String.format("%.2f", progress));
                }
            }
        }
    }

    public interface ProgressListener {
        void onProgress(@NonNull String percentage);
    }
}

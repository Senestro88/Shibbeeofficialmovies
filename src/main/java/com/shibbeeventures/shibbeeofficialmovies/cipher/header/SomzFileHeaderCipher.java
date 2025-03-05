package com.shibbeeventures.shibbeeofficialmovies.cipher.header;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.shibbeeventures.annotations.NonNull;
import com.shibbeeventures.annotations.Nullable;
import com.shibbeeventures.shibbeeofficialmovies.cipher.SomzFileAesCtr;
import com.shibbeeventures.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// USING A RADOM POSITION

public class SomzFileHeaderCipher {
    private final byte[] encKey;
    private static final int sizeOfEncKey = 32;
    private static final int sizeOfHeader = SomzFileHeaderCipherBuffer.sizeOfHeader;
    private static final int sizeOfFileChunk = 1048576; // Process 1MB at a time
    private @Nullable String inputFile;
    private @Nullable String outputFile;
    private @Nullable RandomAccessFile iraFile;
    private @Nullable RandomAccessFile oraFile;
    private HashMap<String, String> inputHeader = new HashMap<>();
    private boolean isEncryptProcess;

    public SomzFileHeaderCipher(@NonNull String encKey) throws IOException {
        if (encKey.isEmpty() || encKey.length() != sizeOfEncKey) {
            throw new IOException("The key must be non-empty and exactly 32 bytes in length.");
        } else {
            this.encKey = encKey.getBytes();
        }
    }

    public void close() {
        if (isResources()) {
            Utils.closeQuietly(iraFile);
            Utils.closeQuietly(oraFile);
            iraFile = null;
            oraFile = null;
            inputFile = null;
            outputFile = null;
        }
        inputHeader = new HashMap<>();
        isEncryptProcess = true;
    }

    public void openFiles(@NonNull String inputFile, @NonNull String outputFile) throws IOException {
        this.iraFile = new RandomAccessFile(inputFile, "r");
        this.oraFile = new RandomAccessFile(outputFile, "rw");
        // Shared lock (Allows multiple readers but prevents writing)
        FileChannel inputChannel = this.iraFile.getChannel();
        inputChannel.lock(0, Long.MAX_VALUE, true);
        // Exclusive lock (Prevents other processes from reading or writing.)
        FileChannel outputChannel = this.oraFile.getChannel();
        outputChannel.tryLock();
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void setProcess(boolean isEncryptProcess) {
        this.isEncryptProcess = isEncryptProcess;
    }

    public void encryptInput(@Nullable ProgressHandler progressHandler) throws IOException {
        if (isEncryptProcess) {
            if (isResources()) {
                HashMap<String, String> header = createHeader();
                int postition = Integer.parseInt(header.get("position"));
                int keysize = Integer.parseInt(inputHeader.get("keysize"));
                saveHeader(header);
                encDecFileProcess(postition, keysize, progressHandler);
            } else {
                throw new IOException("Files must be open.");
            }
        } else {
            throw new IOException("Process must be set to encryption.");
        }
    }

    public HashMap<String, String> readHeaderFromInput() throws IOException {
        if (!isEncryptProcess) {
            if (isResources()) {
                HashMap<String, String> header = readHeader();
                if (!header.isEmpty()) {
                    inputHeader = header;
                    return inputHeader;
                } else {
                    throw new IOException("Failed to read the input file header.");
                }
            } else {
                throw new IOException("Files must be open.");
            }
        } else {
            throw new IOException("Process must be set to decryption.");
        }
    }

    public void decryptInput(@Nullable ProgressHandler progressHandler) throws IOException {
        if (!isEncryptProcess) {
            if (isResources()) {
                if (!inputHeader.isEmpty()) {
                    int postition = Integer.parseInt(inputHeader.get("position"));
                    int keysize = Integer.parseInt(inputHeader.get("keysize"));
                    encDecFileProcess(postition, keysize, progressHandler);
                } else {
                    throw new IOException("You must read input file header.");
                }
            } else {
                throw new IOException("Files must be open.");
            }
        } else {
            throw new IOException("Process must be set to decryption.");
        }
    }

    public int decryptPartFromInput(byte[] buffer, int offset, int length,
            @Nullable ProgressHandler progressHandler)
            throws IOException {
        if (!isEncryptProcess) {
            if (isResources()) {
                if (!inputHeader.isEmpty()) {
                    int postition = Integer.parseInt(inputHeader.get("position"));
                    int keysize = Integer.parseInt(inputHeader.get("keysize"));
                    byte[] rbuffer = new byte[length];
                    int bytesRead = iraFile.read(rbuffer, offset, length);
                    if (bytesRead == -1) {
                        return -1;
                    } else {
                        byte[] dbuffer = encDecProcess(rbuffer, postition, keysize, progressHandler);
                        System.arraycopy(dbuffer, 0, buffer, offset, bytesRead);
                        return bytesRead;
                    }
                } else {
                    throw new IOException("You must read input file header.");
                }
            } else {
                throw new IOException("Files must be open.");
            }
        } else {
            throw new IOException("Process must be set to decryption.");
        }
    }

    // ===================== PRIVATE METHODS ===================== //

    private boolean isResources() {
        return Utils.nonNull(iraFile) && Utils.nonNull(oraFile);
    }

    private int getRandomPositionIndex() {
        return ThreadLocalRandom.current().nextInt(1000, 9001); // Adjusted to include 9000
    }

    private int getRandomKeySize() {
        return ThreadLocalRandom.current().nextInt(10000, 20001); // Adjusted to include 20000
    }

    private HashMap<String, String> createHeader() {
        HashMap<String, String> map = new HashMap<>();
        map.put("position", String.valueOf(getRandomPositionIndex()));
        map.put("keysize", String.valueOf(getRandomKeySize()));
        map.put("version", "1.0");
        map.put("time", String.valueOf(System.currentTimeMillis()));
        return map;
    }

    private void saveHeader(@NonNull HashMap<String, String> header) throws IOException {
        Gson gson = new Gson();
        byte[] paddedHeader = paddHeader(gson.toJson(header).getBytes());
        byte[] ivBytes = Utils.reverse(new String(encKey)).substring(0, 16).getBytes();
        try {
            byte[] encHeader = SomzFileAesCtr.encrypt(paddedHeader, encKey, ivBytes);
            oraFile.write(encHeader);
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    private byte[] paddHeader(byte[] jsonHeader) {
        byte[] paddedHeader = new byte[sizeOfHeader];
        Arrays.fill(paddedHeader, (byte) 0); // Fill with null characters
        System.arraycopy(jsonHeader, 0, paddedHeader, 0, Math.min(jsonHeader.length, sizeOfHeader));
        return paddedHeader;
    }

    private HashMap<String, String> readHeader() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        byte[] buffer = new byte[sizeOfHeader];
        int bytesRead = iraFile.read(buffer);
        if (bytesRead > 0) {
            byte[] ivBytes = Utils.reverse(new String(encKey)).substring(0, 16).getBytes();
            try {
                byte[] decHeader = SomzFileAesCtr.decrypt(buffer, encKey, ivBytes);
                String actualHeader = SomzFileHeaderCipherBuffer.trimNullPadding(new String(decHeader));
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

    private byte[] encDecProcess(byte[] data, int position, int keysize, @Nullable ProgressHandler progressHandler) {
        byte[] keysizeBytes = keysizeToBytes(keysize);
        byte[] output = new byte[data.length];
        int sizeOfKey = keysizeBytes.length;
        int sizeOfData = data.length;
        for (int i = 0; i < sizeOfData; i++) {
            int pos = position % sizeOfKey;
            byte keyByte = keysizeBytes[pos];
            output[i] = (byte) ((data[i] ^ keyByte) & 0xFF); // XOR-based swapping using only the key length
            if (Utils.notNull(progressHandler) && sizeOfData > 0) {
                double progress = ((double) (i + 1) / sizeOfData) * 100;
                progressHandler.onProgress(String.format("%.2f", progress));
            }
        }
        return output;
    }

    private void encDecFileProcess(int position, int keysize, @Nullable ProgressHandler progressHandler)
            throws IOException {
        byte[] keysizeBytes = keysizeToBytes(keysize);
        File file = new File(inputFile);
        long sizeOfFile = file.length();
        long sizeOfProcessedFile = 0;
        byte[] buffer = new byte[sizeOfFileChunk];
        int bytesRead;
        while ((bytesRead = iraFile.read(buffer)) != -1) {
            byte[] chunk = Arrays.copyOf(buffer, bytesRead);
            for (int i = 0; i < chunk.length; i++) {
                int pos = position % keysizeBytes.length;
                byte keyByte = keysizeBytes[pos];
                chunk[i] = (byte) ((chunk[i] ^ keyByte) & 0xFF); // XOR-based swapping using only the key length
            }
            oraFile.write(chunk);
            sizeOfProcessedFile += bytesRead;
            if (Utils.notNull(progressHandler) && sizeOfFile > 0) {
                double progress = ((double) sizeOfProcessedFile / sizeOfFile) * 100;
                progressHandler.onProgress(String.format("%.2f", progress));
            }
        }
    }

    private byte[] keysizeToBytes(int keysize) {
        return new String(String.valueOf(keysize)).getBytes();
    }

    // ===================== PUBLIC ENUM ===================== //
    public interface ProgressHandler {
        void onProgress(@NonNull String percentage);
    }

}

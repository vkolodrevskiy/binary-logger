package com.fugru.logger.reader;

import com.fugru.logger.BinaryLoggable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import static com.fugru.logger.Utils.DELIMITER_SIZE;
import static com.fugru.logger.Utils.byteArrayToInt;

public class BinaryLogFileReader<T extends BinaryLoggable> implements LogReader<T> {
    
    private Class<T> clazz;
    private InputStream inputStream;

    private BinaryLogFileReader() {
    }

    public BinaryLogFileReader(File file, Class<T> clazz) throws IOException {
        this.clazz = clazz;
        this.inputStream = Files.newInputStream(file.toPath());
    }

    @Override
    public Optional<T> readNext() throws Exception {
        byte[] sizeArray;
        T result = null;
        if ((sizeArray = inputStream.readNBytes(DELIMITER_SIZE)).length > 0) {
            int size = byteArrayToInt(sizeArray);
            byte[] payload = new byte[size];
            if (inputStream.read(payload, 0, size) != -1) {
                T instance = clazz.getDeclaredConstructor().newInstance();
                instance.fromBytes(payload);
                result = instance;
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}

package com.fugru.logger;

import java.nio.ByteBuffer;

public class Utils {
    public static final int DELIMITER_SIZE = 4;

    public static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(DELIMITER_SIZE).putInt(i).array();
    }

    public static int byteArrayToInt(byte[] byteArray) {
        ByteBuffer wrapped = ByteBuffer.wrap(byteArray);
        return wrapped.getInt();
    }
}

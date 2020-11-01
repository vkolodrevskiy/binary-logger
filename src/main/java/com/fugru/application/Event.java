package com.fugru.application;

import com.fugru.logger.BinaryLoggable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Event implements BinaryLoggable {
    private Integer id;
    private String content;

    public Event() {
    }

    public Event(Integer id, String content) {
        this.id = id;
        this.content = content;
    }

    public byte[] toBytes() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(bos)) {
            dataOutput.writeInt(id);
            dataOutput.writeUTF(content);
            return bos.toByteArray();
        }
    }

    public void fromBytes(byte[] rawBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(rawBytes);
             DataInputStream dataInput = new DataInputStream(bis)) {
            this.id = dataInput.readInt();
            this.content = dataInput.readUTF();
        }
    }

    @Override
    public String toString() {
        return "Event{" + "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}

package com.otus.java.coursework.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@Data
@NoArgsConstructor
public class ByteMessage implements Externalizable {
    private byte[] content;
    private int length;

    public ByteMessage(byte[] content) {
        this.content = content;
        this.length = content.length;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.length = in.readInt();
        this.content = (byte[]) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(length);
        out.writeObject(content);
    }
}

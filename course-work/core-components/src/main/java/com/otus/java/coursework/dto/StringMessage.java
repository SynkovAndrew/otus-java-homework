package com.otus.java.coursework.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringMessage implements Externalizable {
    private String content;
    private int contentLength;

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.contentLength = in.readInt();
        this.content = (String) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(contentLength);
        out.writeObject(content);
    }
}

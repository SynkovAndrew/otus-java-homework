package com.otus.java.coursework.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@Getter
@Setter
@NoArgsConstructor
public class StringMessage implements Externalizable {
    private String content;
    private int length;

    public StringMessage(String content) {
        this.content = content;
        this.length = content.getBytes().length;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.length = in.readInt();
        this.content = (String) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(length);
        out.writeObject(content);
    }
}

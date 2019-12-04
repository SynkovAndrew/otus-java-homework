package com.otus.java.coursework.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class SerializableObject implements Serializable {
    private final byte[] content;
    private final Class clazz;
}

package com.otus.java.coursework.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chunk {
    private byte[] bytes;
    private boolean isCompleted;
    private boolean isLast;
}
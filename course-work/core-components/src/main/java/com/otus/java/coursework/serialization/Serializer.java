package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.BaseDTO;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class Serializer extends AbstractSerializer {
    @Override
    protected Set<Class<? extends BaseDTO>> getCustomClasses() {
        return Set.of();
    }
}

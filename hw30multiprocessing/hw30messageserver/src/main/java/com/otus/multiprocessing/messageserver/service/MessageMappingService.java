package com.otus.multiprocessing.messageserver.service;

import com.google.gson.Gson;
import dto.ParentDTO;
import messageV2.Message;

import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.substringBetween;

public class MessageMappingService {
    private static final Gson gson = new Gson();

    public static Message<? extends ParentDTO> mapToObject(final String json) {
        final Class<? extends ParentDTO> clazz = (Class<? extends ParentDTO>) getClazz(json);
        final Object object = gson.fromJson(json, clazz);
        return new Message<T>(object);
    }

    private static Class<?> getClazz(final String json) {
        final var className = remove(substringBetween(json, "clazz:", ","), "\"");
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

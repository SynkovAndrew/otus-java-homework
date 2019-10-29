package utils;

import socket.MessageProcessorType;

public class InstanceInfoUtils {
    public static String generateInstanceId(final String host,
                                            final int port,
                                            final MessageProcessorType type) {
        return host + "-" + port + "-" + type + "-id";
    }
}

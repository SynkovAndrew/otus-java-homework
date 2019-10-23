package com.otus.multiprocessing.messageserver.utils;

public class ProcessRunnerUtils {
    public final static String PATH_TO_FRONTEND_CLIENT = "hw30multiprocessing/hw30frontendclient/target/hw30-frontend-client-1.0.jar";
    public final static String PATH_TO_DATABASE_CLIENT = "hw30multiprocessing/hw30databaseclient/target/hw30-database-client-1.0.jar";
    public static int[] FRONTEND_CLIENT_PORTS = new int[]{9000, 9001};
    public static int[] DATABASE_CLIENT_PORTS = new int[]{9010, 9011};

    public static String[] getCommand(final int port, final String pathToJar) {
        return new String[]{
                "java",
                "-jar",
                String.format("-Drun.arguments=--socket.server.port=%d", port),
                pathToJar
        };
    }
}

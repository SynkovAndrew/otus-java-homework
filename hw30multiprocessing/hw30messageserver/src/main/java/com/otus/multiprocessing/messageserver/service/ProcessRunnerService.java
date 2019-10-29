package com.otus.multiprocessing.messageserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
public class ProcessRunnerService {
    @Value("${socket.server.database.ports}")
    public List<Integer> DATABASE_SOCKET_PORTS;
    @Value("${socket.server.frontend.ports}")
    public List<Integer> FRONTEND_SOCKET_PORTS;
    @Value("${web.server.frontend.ports}")
    public List<Integer> FRONTEND_WEB_SERVER_PORTS;
    @Value("${path.to.jar.database}")
    private String PATH_TO_DATABASE_JAR;
    @Value("${path.to.jar.frontend}")
    private String PATH_TO_FRONTEND_JAR;

    private String[] getCommand(final int socketPort, final String pathToJar) {
        return new String[]{
                "java",
                "-Xmx300m",
                "-jar",
                pathToJar,
                String.format("--socket.server.port=%d", socketPort)
        };
    }

    private String[] getCommand(final int socketPort, final int serverPort, final String pathToJar) {
        return new String[]{
                "java",
                "-Xmx300m",
                "-jar",
                pathToJar,
                String.format("--socket.server.port=%d", socketPort),
                String.format("--server.port=%d", serverPort)
        };
    }

    private ProcessBuilder getDatabaseClientProcessBuilder(final int socketPort) {
        return getProcessBuilder(PATH_TO_DATABASE_JAR, socketPort);
    }

    private ProcessBuilder getFrontendClientProcessBuilder(final int socketPort, final int serverPort) {
        return getProcessBuilder(PATH_TO_FRONTEND_JAR, socketPort, serverPort);
    }

    private ProcessBuilder getProcessBuilder(final String pathToJar,
                                             final int socketPort) {
        return new ProcessBuilder(getCommand(socketPort, pathToJar));
    }

    private ProcessBuilder getProcessBuilder(final String pathToJar,
                                             final int socketPort,
                                             final int serverPort) {
        return new ProcessBuilder(getCommand(socketPort, serverPort, pathToJar));
    }

    List<ProcessBuilder> prepareProcesses() {
        return Stream.concat(
                DATABASE_SOCKET_PORTS.stream().map(this::getDatabaseClientProcessBuilder),
                FRONTEND_SOCKET_PORTS.stream().map(port -> getFrontendClientProcessBuilder(
                        port, FRONTEND_WEB_SERVER_PORTS.get(FRONTEND_SOCKET_PORTS.indexOf(port))))
        ).collect(toList());
    }
}

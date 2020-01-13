package com.otus.java.coursework.executor;

import com.otus.java.coursework.dto.StringMessage;
import com.otus.java.coursework.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "file")
public class FileServerRequestExecutor extends AbstractServerRequestExecutor implements ServerRequestExecutor {
    private final String fileName;
    private final FileService fileService;

    public FileServerRequestExecutor(
            final FileService fileService,
            final @Value("${server.action.executor.thread.pool.size:10}") int threadPoolSize,
            final @Value("${file.server.action.executor.file.name:output.txt}") String fileName) {
        super(threadPoolSize);
        this.fileService = fileService;
        this.fileName = fileName;
    }

    @Override
    public void acceptRequest(final int clientId, final StringMessage object) {
        executeRequest(clientId, () -> {
            log.info("Writing data {} from client {} to file...", object, clientId);
            fileService.writeToFile(fileName, object);
            return object;
        });
    }
}

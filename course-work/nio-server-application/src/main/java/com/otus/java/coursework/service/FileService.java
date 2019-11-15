package com.otus.java.coursework.service;

import com.otus.java.coursework.dto.BaseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import static com.otus.java.coursework.utils.Mapper.map;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@Slf4j
@Service
public class FileService {
    @Value("${file.server.action.executor.folder.name}")
    private String folderName;

    private Optional<File> openOrCreateFile(final String folder, final String fileName) {
        final var file = new File(folder + fileName);
        if (file.exists()) {
            return of(file);
        }
        try {
            final var path = Paths.get(folder + fileName);
            return of(Files.createFile(path).toFile());
        } catch (IOException e) {
            log.error("Failed to create file", e);
            return empty();
        }
    }

    public void writeToFile(final String fileName, final BaseDTO dto) {
        openOrCreateFile(folderName, fileName).ifPresent(file ->
                map(dto).ifPresent(json -> {
                    try {
                        Files.write(file.toPath(), (json + "\n").getBytes(), StandardOpenOption.APPEND);
                        log.info("Message {} has been written to file {}", dto, fileName);
                    } catch (IOException e) {
                        log.error("Failed to write text to file", e);
                    }
                }));
    }
}

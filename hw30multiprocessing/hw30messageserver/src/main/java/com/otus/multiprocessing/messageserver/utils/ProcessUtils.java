package com.otus.multiprocessing.messageserver.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class ProcessUtils {

    public static Process start(final ProcessBuilder builder) {
        try {
            return builder.start();
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static void printLogs(final Integer id, final Process process) {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("    < " + id + " > " + line);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

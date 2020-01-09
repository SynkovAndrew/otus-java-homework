package com.otus.java.coursework.service;

import com.otus.java.coursework.utils.Chunk;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ByteProcessorServiceImpl implements ByteProcessorService {
    private final ConcurrentMap<Integer, List<Chunk>> uncompletedChunkMap;

    public ByteProcessorServiceImpl() {
        this.uncompletedChunkMap = new ConcurrentHashMap<>();
    }

    @Override
    public List<byte[]> getCompleteByteSets(final int clientId, final byte[] receivedBytes) {
/*        // get uncompleted chunks
        final List<Chunk> uncompletedChunks = this.uncompletedChunkMap.get(clientId);

        // add all uncompleted chunks to new received bytes
        final int allBytesSize = uncompletedChunks.stream()
                .mapToInt(chunk -> chunk.getBytes().length)
                .sum() + receivedBytes.length;
        final List<byte[]> byteArrayList = Stream
                .concat(uncompletedChunks.stream().map(Chunk::getBytes), Stream.of(receivedBytes))
                .collect(toList());
        final byte[] allBytes = new byte[allBytesSize];
        fill(allBytes, byteArrayList);

        // clear uncompleted chunks
        uncompletedChunks.clear();

        // split all read bytes by defined delimiter
        final SplitResult splitResult = split(BYTE_ARRAY_DELIMITER, allBytes);
        final List<Chunk> chunks = splitResult.getChunks();
        final int currentIterationChunksCount = chunks.size();

        if (currentIterationChunksCount > 0) {
            final Chunk firstChunk = chunks.get(0);
            // if there is no delimiter in the end of last chunk bytes, then this chunk is not completely received
            final Chunk lastChunk = chunks.get(currentIterationChunksCount - 1);
            if (!lastChunk.isCompleted() && !firstChunk.isLast()) {
                uncompletedChunks.add(lastChunk);
            }
            return chunks.stream()
                    .filter(Chunk::isCompleted)
                    .map(Chunk::getBytes)
                    .collect(toList());
        }
        return Collections.emptyList();*/
    }

    @Override
    public void initializeChunk(final int clientId) {
        uncompletedChunkMap.put(clientId, new CopyOnWriteArrayList<>());
    }
}

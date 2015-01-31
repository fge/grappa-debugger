package com.github.fge.grappa.debugger.csvtrace.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

final class BatchLineReader
{
    private final int totalLines;
    private final int bufferSize;
    private final List<String> lines;
    private final BlockingQueue<String> queue;
    private final BufferedReader reader;

    BatchLineReader(final int totalLines, final int bufferSize,
        final BlockingQueue<String> queue, final BufferedReader reader)
    {
        this.totalLines = totalLines;
        this.bufferSize = bufferSize;
        this.queue = Objects.requireNonNull(queue);
        this.reader = Objects.requireNonNull(reader);

        lines = new ArrayList<>(bufferSize);
    }

    void process()
        throws IOException, InterruptedException
    {
        int remaining = totalLines;
        int toRead;

        while (remaining > 0) {
            toRead = Math.min(remaining, bufferSize);
            for (int i = 0; i < toRead; i++)
                lines.add(reader.readLine());
            queue.put(lines.stream().collect(Collectors.joining("\n")));
            lines.clear();
            remaining -= bufferSize;
        }
    }
}

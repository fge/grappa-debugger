package com.github.parboiled1.grappa.debugger.parser;

import com.github.parboiled1.grappa.run.EventBasedParseRunner;
import com.github.parboiled1.grappa.trace.TracingParseRunnerListener;
import org.parboiled.Parboiled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class GenTrace
{
    public static void main(final String... args)
        throws IOException
    {
        final TestParser parser = Parboiled.createParser(TestParser.class);
        final EventBasedParseRunner<String> runner
            = new EventBasedParseRunner<>(parser.quotedString());
        final String zipPath = "/tmp/trace.zip";
        Files.deleteIfExists(Paths.get(zipPath));
        try (
            final TracingParseRunnerListener<String> listener
                = new TracingParseRunnerListener<>(zipPath);
        ) {
            runner.registerListener(listener);
            runner.run("\"A valid \\\"quoted string\\\" inside\"");
        }
        System.out.println("done");
    }
}

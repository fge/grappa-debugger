package com.github.fge.grappa.debugger.web;

import spark.Spark;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SparkTesting
{
    public static void main(final String... args)
    {
        Spark.staticFileLocation("/site");

        Spark.port(8080);

        Spark.post("/upload", (request, response) -> {
            final Path tempFile = Files.createTempFile("foo", ".bar");
            try (
                final InputStream in = request.raw().getInputStream();
                final OutputStream out = Files.newOutputStream(tempFile);
            ) {
                final byte[] buf = new byte[16384];

                int nrBytes;
                while ((nrBytes = in.read(buf)) != -1)
                    out.write(buf, 0, nrBytes);
            }
            System.out.println("file saved as " + tempFile);
            response.redirect("/");
            return "OK";
        });
    }
}

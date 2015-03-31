package com.github.fge.grappa.debugger.web;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import spark.Spark;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SparkTesting
{

    public static void main(final String... args)
    {
        Spark.staticFileLocation("/site");

        Spark.port(8080);

        Spark.post("/upload", (request, response) -> {
            final MultipartParser parser
                = new MultipartParser(request.raw(), Integer.MAX_VALUE);

            final Part part = parser.readNextPart();

            final Path path = Paths.get("/tmp/meh");

            try (
                final OutputStream out = Files.newOutputStream(path);
            ) {
                ((FilePart) part).writeTo(out);
            }

            response.redirect("/");
            return "OK";
        });
    }
}

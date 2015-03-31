package com.github.fge.grappa.debugger.web;

import spark.Spark;

public final class SparkTesting
{
    public static void main(final String... args)
    {
        Spark.staticFileLocation("/static");

        Spark.port(8080);

        Spark.get("/", (request, response) -> "hello");

    }
}

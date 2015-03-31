package com.github.fge.grappa.debugger.web;

import spark.Spark;

public final class SparkTesting
{
    public static void main(final String... args)
    {
        Spark.staticFileLocation("/site");

        Spark.port(8080);

        Spark.post("/upload", (request, response) -> {
            System.out.println(request.queryParams());
            response.redirect("/");
            return "OK";
        });
    }
}

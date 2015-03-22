package com.github.fge.grappa.debugger.database;

import java.io.IOException;
import java.nio.file.Path;

public interface TraceModelFactory
{
    TraceModel getModel(Path path)
        throws IOException;
}

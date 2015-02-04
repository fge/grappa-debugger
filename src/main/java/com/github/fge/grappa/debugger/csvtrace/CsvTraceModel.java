package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.csvtrace.newmodel.InputText;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTree;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CsvTraceModel
{
    @Nonnull
    ParseInfo getParseInfo();

    @Nonnull
    InputText getInputText()
        throws IOException;

    @Nullable
    ParseTree getParseTree()
        throws ExecutionException, IOException;

    @Nonnull
    List<ParseTreeNode> getNodeChildren(int nodeId)
        throws ExecutionException;

    void dispose()
        throws Exception;
}

package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.csvtrace.newmodel.InputText;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTree;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public interface CsvTraceModel
{
    InputText getInputText()
        throws IOException;

    void dispose()
        throws IOException;

    @Nonnull
    ParseTree getParseTree()
        throws IOException;

    List<ParseTreeNode> getNodeChildren(int nodeId);
}

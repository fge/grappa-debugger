package com.github.parboiled1.grappa.debugger.parser;

import javafx.scene.control.TreeItem;

import java.util.Collections;
import java.util.Map;

public final class ParsingRunResult
{
    private final TreeItem<String> root;
    private final Map<TreeItem<String>, String> details;

    ParsingRunResult(final TreeItem<String> root,
        final Map<TreeItem<String>, String> details)
    {
        this.root = root;
        this.details = Collections.unmodifiableMap(details);
    }

    public TreeItem<String> getRoot()
    {
        return root;
    }

    public Map<TreeItem<String>, String> getDetails()
    {
        return details;
    }
}

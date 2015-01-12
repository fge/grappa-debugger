package com.github.parboiled1.grappa.debugger.parser;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.run.MatchFailureEvent;
import com.github.parboiled1.grappa.run.MatchSuccessEvent;
import com.github.parboiled1.grappa.run.PreMatchEvent;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import javafx.scene.control.TreeItem;
import org.parboiled.Context;
import org.parboiled.MatcherContext;
import org.parboiled.Node;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.Position;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class MatchEventListenerTree
{
    private final Map<MatchId, TreeItem<String>> treeItems
        = new HashMap<>();
    private final Map<TreeItem<String>, String> details
        = new IdentityHashMap<>();

    private TreeItem<String> root;

    @Subscribe
    public void preMatch(final PreMatchEvent<?> event)
    {
        final MatcherContext<?> context = event.getContext();
        final MatchId id = new MatchId(context);
        final TreeItem<String> item = new TreeItem<>();
        item.setValue(context.getMatcher().toString());

        treeItems.put(id, item);

        final int level = id.level;
        if (level == 0) {
            root = item;
            return;
        }

        final MatchId parentId = new MatchId(context.getParent());
        treeItems.get(parentId).getChildren().add(item);
    }

    @Subscribe
    public void failure(final MatchFailureEvent<?> event)
    {
        final MatcherContext<?> context = event.getContext();
        final MatchId id = new MatchId(context);
        final TreeItem<String> item = treeItems.get(id);
        final String value = item.getValue();
        item.setValue(value + " (FAILED)");
        details.put(item, failureDetails(context));
    }

    @Subscribe
    public void success(final MatchSuccessEvent<?> event)
    {
        final MatcherContext<?> context = event.getContext();
        final MatchId id = new MatchId(context);
        final TreeItem<String> item = treeItems.get(id);
        final String value = item.getValue();
        item.setValue(value + " (SUCCESS)");
        details.put(item, successDetails(context));
    }

    private String successDetails(final MatcherContext<?> context)
    {
        final StringBuilder sb = new StringBuilder();
        final InputBuffer buffer = context.getInputBuffer();
        final Node<?> node = context.getNode();
        final int start = node.getStartIndex();
        final int end = node.getEndIndex();
        final Position position = buffer.getPosition(start);
            sb.append("matched text:\n<")
                .append(buffer.extract(start, end))
                .append(">\n at line ").append(position.getLine())
                .append(", column ").append(position.getColumn());
        return sb.toString();
    }

    public ParsingRunResult getResult()
    {
        return new ParsingRunResult(root, details);
    }

    private static String failureDetails(final MatcherContext<?> context)
    {
        final StringBuilder sb = new StringBuilder();
        final InputBuffer buffer = context.getInputBuffer();
        final int start = context.getCurrentIndex();
        final Position position = buffer.getPosition(start);
        sb.append("failed to match at line ").append(position.getLine())
            .append(", column ").append(position.getColumn())
            .append("\n:")
            .append(buffer.extractLine(position.getLine()))
            .append(Strings.repeat(" ", position.getColumn()))
            .append('^');
        return sb.toString();
    }

    private static final class MatchId
    {
        private final MatcherPath path;
        private final int level;
        private final int startIndex;

        private MatchId(final Context<?> context)
        {
            path = context.getPath();
            level = context.getLevel();
            startIndex = context.getStartIndex();
        }

        @Override
        public int hashCode()
        {
            return startIndex ^ level ^ path.hashCode();
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (obj == null)
                return false;
            if (this == obj)
                return true;
            if (getClass() != obj.getClass())
                return false;
            final MatchId other = (MatchId) obj;
            return startIndex == other.startIndex
                && level == other.level
                && Objects.equals(path, other.path);
        }

        @Nonnull
        @Override
        public String toString()
        {
            return MoreObjects.toStringHelper(this)
                .add("level", level)
                .add("startIndex", startIndex)
                .add("path", path)
                .toString();
        }
    }
}

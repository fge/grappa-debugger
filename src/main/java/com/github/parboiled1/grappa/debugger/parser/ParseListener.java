package com.github.parboiled1.grappa.debugger.parser;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.run.MatchFailureEvent;
import com.github.parboiled1.grappa.run.MatchSuccessEvent;
import com.github.parboiled1.grappa.run.ParseRunnerListener;
import com.github.parboiled1.grappa.run.PreMatchEvent;
import com.google.common.base.MoreObjects;
import javafx.scene.control.TreeItem;
import org.parboiled.Context;
import org.parboiled.MatcherContext;
import org.parboiled.Node;
import org.parboiled.support.MatcherPath;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ParseListener<V>
    extends ParseRunnerListener<V>
{
    private final Map<MatchId, TreeItem<MatchResult>> treeItems
        = new HashMap<>();

    private TreeItem<MatchResult> root;

    @Override
    public void beforeMatch(final PreMatchEvent<V> event)
    {
        final MatcherContext<V> context = event.getContext();
        final MatchId id = new MatchId(context);
        final TreeItem<MatchResult> item = new TreeItem<>();

        treeItems.put(id, item);

        final int level = id.level;
        if (level == 0) {
            root = item;
            return;
        }

        final MatchId parentId = new MatchId(context.getParent());
        treeItems.get(parentId).getChildren().add(item);
    }

    @Override
    public void matchFailure(final MatchFailureEvent<V> event)
    {
        final MatcherContext<V> context = event.getContext();
        final MatchId id = new MatchId(context);
        final TreeItem<MatchResult> item = treeItems.get(id);
        final InputBuffer buffer = context.getInputBuffer();
        final String label = context.getMatcher().getLabel();
        final Node<?> parsingNode = context.getNode();
        final MatchResult result = new MatchResult(label, false, buffer, parsingNode);
        item.setValue(result);
    }

    @Override
    public void matchSuccess(final MatchSuccessEvent<V> event)
    {
        final MatcherContext<V> context = event.getContext();
        final MatchId id = new MatchId(context);
        final TreeItem<MatchResult> item = treeItems.get(id);
        final InputBuffer buffer = context.getInputBuffer();
        final String label = context.getMatcher().getLabel();
        final Node<V> parsingNode = context.getNode();
        final MatchResult result = new MatchResult(label, true, buffer, parsingNode);
        item.setValue(result);
    }

    public TreeItem<MatchResult> getRoot()
    {
        return root;
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

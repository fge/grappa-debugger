package com.github.parboiled1.grappa.debugger.parser;

import com.github.parboiled1.grappa.run.MatchFailureEvent;
import com.github.parboiled1.grappa.run.MatchSuccessEvent;
import com.github.parboiled1.grappa.run.PreMatchEvent;
import com.google.common.base.MoreObjects;
import com.google.common.eventbus.Subscribe;
import javafx.scene.control.TreeItem;
import org.parboiled.Context;
import org.parboiled.MatcherContext;
import org.parboiled.support.MatcherPath;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MatchEventListenerTree
{
    private final Map<MatchId, TreeItem<String>> treeItems
        = new HashMap<>();
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
    }

    @Subscribe
    public void success(final MatchSuccessEvent<?> event)
    {
        final MatcherContext<?> context = event.getContext();
        final MatchId id = new MatchId(context);
        final TreeItem<String> item = treeItems.get(id);
        final String value = item.getValue();
        item.setValue(value + " (SUCCESS)");
    }

    public TreeItem<String> getRoot()
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

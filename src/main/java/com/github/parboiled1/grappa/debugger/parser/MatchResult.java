package com.github.parboiled1.grappa.debugger.parser;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.debugger.internal.NonFinalForTesting;
import com.google.common.base.Strings;
import org.parboiled.Node;
import org.parboiled.support.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class MatchResult
{
    private final String label;
    private final boolean success;
    private final InputBuffer buffer;
    private final Node<?> parsingNode;

    MatchResult(final String label, final boolean success,
        final InputBuffer buffer, @Nullable final Node<?> parsingNode)
    {
        this.label = label;
        this.success = success;
        this.buffer = buffer;
        this.parsingNode = parsingNode;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getLabel()
    {
        return label;
    }

    public InputBuffer getBuffer()
    {
        return buffer;
    }

    public Node<?> getParsingNode()
    {
        return parsingNode;
    }

    @Nonnull
    @Override
    public String toString()
    {
        if (parsingNode == null)
            return "no information available";
        final int start = parsingNode.getStartIndex();
        final Position pos = buffer.getPosition(start);
        final int line = pos.getLine();
        final int column = pos.getColumn();

        final StringBuilder sb = new StringBuilder("Match information:\n");
        sb.append("Matcher: ").append(parsingNode.getMatcher());
        sb.append("\nStarting position: line ").append(line)
            .append(", column ").append(column)
            .append("\n----\n").append(buffer.extractLine(line))
            .append('\n')
            .append(Strings.repeat(" ", column - 1)).append("^\n----\n");
        if (success) {
            sb.append("Match SUCCESS; text matched:\n<")
                .append(buffer.extract(start, parsingNode.getEndIndex()))
                .append('>');
        } else {
            sb.append("Match FAILED");
        }

        return sb.toString();
    }
}

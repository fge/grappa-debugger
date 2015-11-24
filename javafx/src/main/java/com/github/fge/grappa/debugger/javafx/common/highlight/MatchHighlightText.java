package com.github.fge.grappa.debugger.javafx.common.highlight;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.javafx.common.JavafxUtils;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public abstract class MatchHighlightText
{
    private static final Pattern CRLF = Pattern.compile("\r\n");
    private static final Pattern LF_NO_CR = Pattern.compile("(?<!\r)\n");

    private final InputBuffer buffer;
    private final int startIndex;
    private final int endIndex;
    private final String matchStyle;

    protected MatchHighlightText(final InputBuffer buffer, final int startIndex,
        final int endIndex, final String matchStyle)
    {
        this.buffer = buffer;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.matchStyle = matchStyle;
    }

    protected final String textBeforeMatch()
    {
        return CRLF.matcher(buffer.extract(0, startIndex)).replaceAll("\n");
    }

    // TODO: rewrite... How?
    protected final String matchedText()
    {
        final String extract = buffer.extract(startIndex, endIndex);
        final int len = extract.length();
        final StringBuilder sb = new StringBuilder(len);

        char c;
        boolean seenCR = false;
        boolean seenLF = false;

        for (int index = 0; index < len; index++) {
            c = extract.charAt(index);
            switch (c) {
                case '\r':
                    seenCR = true;
                    break;
                case '\n':
                    if (seenLF)
                        sb.append("\\n\n");
                    seenLF = true;
                    break;
                default:
                    if (seenCR || seenLF) {
                        if (seenCR)
                            sb.append("\\r");
                        if (seenLF)
                            sb.append("\\n");
                        sb.append('\n');
                    }
                    sb.append(c);
                    seenCR = false;
                    seenLF = false;
            }
        }

        // Catch up...

        if (seenCR || seenLF) {
            if (seenCR)
                sb.append("\\r");
            if (seenLF)
                sb.append("\\n");
            sb.append('\n');
        }

        return sb.toString();
    }

    protected abstract String decoratedMatchText();

    protected final String textAfterMatch()
    {
        return CRLF.matcher(buffer.extract(endIndex, buffer.length()))
            .replaceAll("\n");
    }

    public final String fullText()
    {
        return textBeforeMatch() + decoratedMatchText() + textAfterMatch();
    }

    public final StyleSpans<Collection<String>> getStyleSpans()
    {
        String text;
        int length;

        final StyleSpansBuilder<Collection<String>> builder
            = new StyleSpansBuilder<>(3);

        text = textBeforeMatch();
        length = text.length();
        builder.add(JavafxUtils.STYLE_BEFOREMATCH, length);

        text = matchedText();
        length = text.length();
        builder.add(Collections.singleton(matchStyle), length);

        text = textAfterMatch();
        length = text.length();
        builder.add(JavafxUtils.STYLE_AFTERMATCH, length);

        return builder.create();
    }
}

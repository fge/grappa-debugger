package com.github.fge.grappa.debugger.javafx.common.highlight;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class MatchHighlightTextTest
{
    private static final String STYLE = "foo";

    private static final class TestMatchHighlightText
        extends MatchHighlightText
    {

        private TestMatchHighlightText(final String input, final int startIndex,
            final int endIndex)
        {
            super(new CharSequenceInputBuffer(input), startIndex, endIndex,
                STYLE);
        }


        @Override
        protected String decoratedMatchText()
        {
            return null;
        }
    }

    @DataProvider
    public Iterator<Object[]> testData()
    {
        final List<Object[]> list = new ArrayList<>();

        String input;
        int startIndex;
        int endIndex;
        String beforeMatch;
        String matchedText;
        String afterMatch;

        input = "abc";
        startIndex = 1;
        endIndex = 2;
        beforeMatch = "a";
        matchedText = "b";
        afterMatch = "c";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
        matchedText, afterMatch});

        input = "abc";
        startIndex = 0;
        endIndex = 2;
        beforeMatch = "";
        matchedText = "ab";
        afterMatch = "c";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        input = "abc";
        startIndex = 0;
        endIndex = 3;
        beforeMatch = "";
        matchedText = "abc";
        afterMatch = "";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        input = "a\nbc";
        startIndex = 2;
        endIndex = 2;
        beforeMatch = "a\n";
        matchedText = "";
        afterMatch = "bc";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        input = "a\nbc";
        startIndex = 1;
        endIndex = 3;
        beforeMatch = "a";
        matchedText = "\\n\nb";
        afterMatch = "c";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        input = "a\r\nbc";
        startIndex = 3;
        endIndex = 4;
        beforeMatch = "a\n";
        matchedText = "b";
        afterMatch = "c";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        input = "ab\r\nc";
        startIndex = 1;
        endIndex = 2;
        beforeMatch = "a";
        matchedText = "b";
        afterMatch = "\nc";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        input = "ab\r\nc";
        startIndex = 1;
        endIndex = 4;
        beforeMatch = "a";
        matchedText = "b\\r\\n\n";
        afterMatch = "c";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        input = "abc";
        startIndex = 1;
        endIndex = 1;
        beforeMatch = "a";
        matchedText = "";
        afterMatch = "bc";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        input = "ab\n\nc";
        startIndex = 1;
        endIndex = 4;
        beforeMatch = "a";
        matchedText = "b\\n\n\\n\n";
        afterMatch = "c";
        list.add(new Object[] { input, startIndex, endIndex, beforeMatch,
            matchedText, afterMatch});

        Collections.shuffle(list);
        return list.iterator();
    }

    @Test(dataProvider = "testData")
    public void matchHighlighterTest(final String input, final int startIndex,
        final int endIndex, final String beforeMatch, final String matchedText,
        final String afterMatch)
    {
        final MatchHighlightText highlightText
            = new TestMatchHighlightText(input, startIndex, endIndex);

        assertThat(highlightText.textBeforeMatch())
            .as("text before match, with cr/lf substitutions")
            .isEqualTo(beforeMatch);
        assertThat(highlightText.matchedText())
            .as("matched text, with cr/lf substitutions")
            .isEqualTo(matchedText);
        assertThat(highlightText.textAfterMatch())
            .as("text after match, with cr/lf substitutions")
            .isEqualTo(afterMatch);
    }
}

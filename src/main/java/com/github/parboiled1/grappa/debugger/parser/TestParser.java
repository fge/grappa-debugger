package com.github.parboiled1.grappa.debugger.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.Label;

public class TestParser
    extends BaseParser<String>
{
    @Label("unquoted")
    public Rule unquoted()
    {
        return oneOrMore(noneOf("\\\""));
    }

    @Label("quoted")
    public Rule quoted()
    {
        return sequence('\\', ANY);
    }

    @Label("content")
    public Rule content()
    {
        return join(unquoted()).using(quoted()).min(0);
    }

    @Label("quotedString")
    public Rule quotedString()
    {
        return sequence('"', content(), '"', EOI);
    }
}

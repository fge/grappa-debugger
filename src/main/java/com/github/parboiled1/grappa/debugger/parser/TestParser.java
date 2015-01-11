package com.github.parboiled1.grappa.debugger.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;

public class TestParser
    extends BaseParser<String>
{
    public Rule unquoted()
    {
        return oneOrMore(noneOf("\\\""));
    }

    public Rule quoted()
    {
        return sequence('\\', ANY);
    }

    public Rule content()
    {
        return join(unquoted()).using(quoted()).min(0);
    }

    public Rule quotedString()
    {
        return sequence('"', content(), '"', EOI);
    }
}

package com.github.fge.grappa.debugger.postgresql.model;

import com.github.fge.grappa.debugger.model.tree.RuleInfo;
import com.github.fge.grappa.matchers.MatcherType;
import org.jooq.Converter;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.EnumConverter;

import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.MATCHERS;

public final class RuleInfoMapper
    implements RecordMapper<Record, RuleInfo>
{
    public static final RecordMapper<Record, RuleInfo> INSTANCE
        = new RuleInfoMapper();

    private static final Converter<String, MatcherType> CONVERTER
        = new EnumConverter<>(String.class, MatcherType.class);

    private RuleInfoMapper()
    {
    }

    @Override
    public RuleInfo map(final Record record)
    {
        return new RuleInfo(
            record.getValue(MATCHERS.CLASS_NAME),
            record.getValue(MATCHERS.MATCHER_TYPE, CONVERTER),
            record.getValue(MATCHERS.NAME)
        );
    }
}

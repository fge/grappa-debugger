package com.github.fge.grappa.debugger.common.db;

import com.github.fge.grappa.debugger.csvtrace.newmodel.RuleInfo;
import com.github.fge.grappa.debugger.jooq.Tables;
import com.github.fge.grappa.matchers.MatcherType;
import org.jooq.Converter;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.EnumConverter;

public final class RuleInfoMapper
    implements RecordMapper<Record, RuleInfo>
{
    private static final Converter<String, MatcherType> CONVERTER
        = new EnumConverter<>(String.class, MatcherType.class);

    @Override
    public RuleInfo map(final Record record)
    {
        return new RuleInfo(
            record.getValue(Tables.MATCHERS.CLASS_NAME),
            record.getValue(Tables.MATCHERS.MATCHER_TYPE, CONVERTER),
            record.getValue(Tables.MATCHERS.NAME)
        );
    }
}

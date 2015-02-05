package com.github.fge.grappa.debugger.common.db;

import com.github.fge.grappa.debugger.csvtrace.newmodel.RuleInfo;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class RuleInvocationStatisticsMapper
    implements RecordMapper<Record, RuleInvocationStatistics>
{
    private final RecordMapper<Record, RuleInfo> ruleInfoMapper
        = new RuleInfoMapper();
    /**
     * A callback method indicating that the next record has been fetched.
     *
     * @param record The record to be mapped. This is never null.
     */
    @SuppressWarnings("AutoUnboxing")
    @Override
    public RuleInvocationStatistics map(final Record record)
    {
        final RuleInfo info = ruleInfoMapper.map(record);
        return new RuleInvocationStatistics(info,
            record.getValue("nonEmptyMatches", Integer.class),
            record.getValue("emptyMatches", Integer.class),
            record.getValue("failedMatches", Integer.class)
        );
    }
}

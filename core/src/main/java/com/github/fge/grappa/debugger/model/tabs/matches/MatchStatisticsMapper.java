package com.github.fge.grappa.debugger.model.tabs.matches;

import com.github.fge.grappa.debugger.model.common.RuleInfo;
import com.github.fge.grappa.debugger.model.common.RuleInfoMapper;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class MatchStatisticsMapper
    implements RecordMapper<Record, MatchStatistics>
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
    public MatchStatistics map(final Record record)
    {
        final RuleInfo info = ruleInfoMapper.map(record);
        return new MatchStatistics(info,
            record.getValue("nonEmptyMatches", Integer.class),
            record.getValue("emptyMatches", Integer.class),
            record.getValue("failedMatches", Integer.class)
        );
    }
}

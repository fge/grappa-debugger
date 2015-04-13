package com.github.fge.grappa.debugger.postgresql.model;

import com.github.fge.grappa.debugger.model.matches.MatchStatistics;
import com.github.fge.grappa.debugger.model.tree.RuleInfo;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class MatchStatisticsMapper
    implements RecordMapper<Record, MatchStatistics>
{
    public static final RecordMapper<Record, MatchStatistics> INSTANCE
        = new MatchStatisticsMapper();

    private final RecordMapper<Record, RuleInfo> ruleInfoMapper
        = RuleInfoMapper.INSTANCE;

    private MatchStatisticsMapper()
    {
    }

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

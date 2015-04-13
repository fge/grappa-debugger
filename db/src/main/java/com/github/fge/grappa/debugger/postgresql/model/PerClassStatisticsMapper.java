package com.github.fge.grappa.debugger.postgresql.model;

import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.MATCHERS;

public final class PerClassStatisticsMapper
    implements RecordMapper<Record, PerClassStatistics>
{
    public static final RecordMapper<Record, PerClassStatistics> INSTANCE
        = new PerClassStatisticsMapper();

    private PerClassStatisticsMapper()
    {
    }

    @Override
    public PerClassStatistics map(final Record record)
    {
        //noinspection AutoUnboxing
        return new PerClassStatistics(
            record.getValue(MATCHERS.CLASS_NAME),
            record.getValue("nrRules", Integer.class),
            record.getValue("nrCalls", Integer.class)
        );
    }
}

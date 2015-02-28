package com.github.fge.grappa.debugger.model.db;

import com.github.fge.grappa.debugger.jooq.Tables;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class PerClassStatisticsMapper
    implements RecordMapper<Record, PerClassStatistics>
{
    @Override
    public PerClassStatistics map(final Record record)
    {
        //noinspection AutoUnboxing
        return new PerClassStatistics(
            record.getValue(Tables.MATCHERS.CLASS_NAME),
            record.getValue("nrRules", Integer.class),
            record.getValue("nrCalls", Integer.class)
        );
    }
}

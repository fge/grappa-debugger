package com.github.fge.grappa.debugger.common.db;

import com.github.fge.grappa.debugger.csvtrace.newmodel.LineMatcherStatus;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class LineMatcherStatusMapper
    implements RecordMapper<Record, LineMatcherStatus>
{
    /**
     * A callback method indicating that the next record has been fetched.
     *
     * @param record The record to be mapped. This is never null.
     */
    @SuppressWarnings("AutoUnboxing")
    @Override
    public LineMatcherStatus map(final Record record)
    {
        return new LineMatcherStatus(
            record.getValue("nrWaiting", Integer.class),
            record.getValue("nrStarted", Integer.class),
            record.getValue("nrSuccess", Integer.class),
            record.getValue("nrFailures", Integer.class)
        );
    }
}

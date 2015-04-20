package com.github.fge.grappa.debugger.zip2db;


import com.github.fge.grappa.debugger.h2.db.load.CsvLineRecord;
import com.github.fge.grappa.debugger.postgresql.jooq.tables.records
    .MatchersRecord;

import java.util.UUID;

public final class CsvMatchersRecord
    extends CsvLineRecord<MatchersRecord>
{
    private final UUID uuid;

    public CsvMatchersRecord(final UUID uuid)
    {
        super(4);
        this.uuid = uuid;
    }

    @SuppressWarnings({ "MethodCanBeVariableArityMethod", "AutoBoxing" })
    @Override
    protected MatchersRecord doApply(final String[] parts)
    {
        final MatchersRecord record = new MatchersRecord();
        record.setParseInfoId(uuid);
        record.setId(Integer.parseInt(parts[0]));
        record.setClassName(parts[1]);
        record.setMatcherType(parts[2]);
        record.setName(parts[3]);

        return record;
    }
}

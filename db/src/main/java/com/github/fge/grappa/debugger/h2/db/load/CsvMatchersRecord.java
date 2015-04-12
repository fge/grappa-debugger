package com.github.fge.grappa.debugger.h2.db.load;


import com.github.fge.grappa.debugger.postgresql.jooq.tables.records.MatchersRecord;

public final class CsvMatchersRecord
    extends CsvLineRecord<MatchersRecord>
{
    public CsvMatchersRecord()
    {
        super(4);
    }

    @SuppressWarnings({ "MethodCanBeVariableArityMethod", "AutoBoxing" })
    @Override
    protected MatchersRecord doApply(final String[] parts)
    {
        final MatchersRecord record = new MatchersRecord();
        record.setId(Integer.parseInt(parts[0]));
        record.setClassName(parts[1]);
        record.setMatcherType(parts[2]);
        record.setName(parts[3]);

        return record;
    }
}

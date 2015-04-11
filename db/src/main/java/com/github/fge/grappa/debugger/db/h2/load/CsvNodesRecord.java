package com.github.fge.grappa.debugger.db.h2.load;


import com.github.fge.grappa.debugger.jooq.postgresql.tables.records
    .NodesRecord;

public final class CsvNodesRecord
    extends CsvLineRecord<NodesRecord>
{
    @SuppressWarnings({ "MethodCanBeVariableArityMethod", "AutoBoxing" })
    @Override
    protected NodesRecord doApply(final String[] parts)
    {
        final NodesRecord record = new NodesRecord();

        record.setId(Integer.parseInt(parts[1]));
        record.setParentId(Integer.parseInt(parts[0]));
        record.setLevel(Integer.parseInt(parts[2]));
        record.setSuccess(Integer.parseInt(parts[3]));
        record.setMatcherId(Integer.parseInt(parts[4]));
        record.setStartIndex(Integer.parseInt(parts[5]));
        record.setEndIndex(Integer.parseInt(parts[6]));
        record.setTime(Long.parseLong(parts[7]));

        return record;
    }
}

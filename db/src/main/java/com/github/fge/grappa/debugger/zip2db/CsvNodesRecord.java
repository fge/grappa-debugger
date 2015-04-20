package com.github.fge.grappa.debugger.zip2db;


import com.github.fge.grappa.debugger.h2.db.load.CsvLineRecord;
import com.github.fge.grappa.debugger.postgresql.jooq.tables.records
    .NodesRecord;

import java.util.UUID;

public final class CsvNodesRecord
    extends CsvLineRecord<NodesRecord>
{
    private final UUID uuid;

    public CsvNodesRecord(final UUID uuid)
    {
        this.uuid = uuid;
    }

    @SuppressWarnings({ "MethodCanBeVariableArityMethod", "AutoBoxing" })
    @Override
    protected NodesRecord doApply(final String[] parts)
    {
        final NodesRecord record = new NodesRecord();

        record.setParseInfoId(uuid);
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

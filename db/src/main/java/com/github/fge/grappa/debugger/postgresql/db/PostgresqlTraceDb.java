package com.github.fge.grappa.debugger.postgresql.db;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.postgresql.jooq.tables.records
    .ParseInfoRecord;
import com.github.fge.grappa.debugger.postgresql.model.PostgresqlTraceModel;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.NODES;
import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.PARSE_INFO;

public final class PostgresqlTraceDb
    implements TraceDb
{
    private final TraceDbLoadStatus status = new PostgresqlTraceDbLoadStatus();

    private final DSLContext jooq;
    private final UUID uuid;
    private final ParseInfo parseInfo;
    private final InputBuffer inputBuffer;

    @SuppressWarnings("AutoUnboxing")
    public PostgresqlTraceDb(final DSLContext jooq, final UUID uuid)
    {
        this.jooq = jooq;
        this.uuid = uuid;

        final ParseInfoRecord record = jooq.selectFrom(PARSE_INFO)
            .where(PARSE_INFO.ID.eq(uuid)).fetchOne();

        final int treeDepth = jooq.selectDistinct(NODES.LEVEL)
            .from(NODES).where(PARSE_INFO.ID.eq(uuid)).fetchOne().value1();

        final int nrNodes = jooq.selectCount().from(NODES)
            .where(PARSE_INFO.ID.eq(uuid)).fetchOne().value1();

        final int nrMatchers = jooq.selectCount().from(MATCHERS)
            .where(PARSE_INFO.ID.eq(uuid)).fetchOne().value1();

        final LocalDateTime time = record.getDate().toLocalDateTime();
        final String content = record.getContent();
        inputBuffer = new CharSequenceInputBuffer(content);

        final int length = content.length();
        final int nrLines = inputBuffer.getLineCount();
        final int nrCodePoints = content.codePointCount(0, length);

        parseInfo = new ParseInfo(time, treeDepth, nrMatchers, nrLines, length,
            nrCodePoints, nrNodes);
    }

    @Override
    public TraceDbLoadStatus getLoadStatus()
    {
        return status;
    }

    @Override
    public ParseInfo getParseInfo()
    {
        return parseInfo;
    }

    @Override
    public InputBuffer getInputBuffer()
    {
        return inputBuffer;
    }

    @Override
    public TraceModel getModel()
    {
        return new PostgresqlTraceModel(uuid, jooq, inputBuffer);
    }

    @Override
    public void close()
    {
    }
}

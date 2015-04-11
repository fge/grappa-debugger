package com.github.fge.grappa.debugger.db.postgresql;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.jooq.postgresql.tables.records.ParseInfoRecord;
import com.github.fge.grappa.debugger.model.tree.InputText;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.fge.grappa.debugger.jooq.postgresql.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.jooq.postgresql.Tables.NODES;
import static com.github.fge.grappa.debugger.jooq.postgresql.Tables.PARSE_INFO;

public final class PostgresqlTraceDb
    implements TraceDb
{
    private final TraceDbLoadStatus status = new PostgresqlTraceDbLoadStatus();

    private final DSLContext jooq;
    private final UUID uuid;
    private final ParseInfo parseInfo;
    private final InputText inputText;

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
        final InputBuffer buffer = new CharSequenceInputBuffer(content);

        final int length = buffer.length();
        final int nrLines = buffer.getLineCount();
        final int nrCodePoints = content.codePointCount(0, length);

        inputText = new InputText(nrLines, length, nrCodePoints, buffer);
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
    public InputText getInputText()
    {
        return inputText;
    }

    @Override
    public DSLContext getJooq()
    {
        return jooq;
    }

    @Override
    public void close()
    {
    }
}

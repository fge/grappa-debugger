package com.github.fge.grappa.debugger.jooq.tables;

import com.github.fge.grappa.debugger.jooq.Keys;
import com.github.fge.grappa.debugger.jooq.Public;
import com.github.fge.grappa.debugger.jooq.tables.records.NodesRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "PublicField", "ThisEscapedInObjectConstruction" })
public class Nodes
    extends TableImpl<NodesRecord>
{
    public static final Nodes NODES = new Nodes();

    @Override
    public Class<NodesRecord> getRecordType()
    {
        return NodesRecord.class;
    }

    public final TableField<NodesRecord, Integer> ID
        = createField("ID", SQLDataType.INTEGER.nullable(false), this, "");

    public final TableField<NodesRecord, Integer> PARENT_ID
        = createField("PARENT_ID", SQLDataType.INTEGER.nullable(false), this,
        "");

    public final TableField<NodesRecord, Integer> LEVEL
        = createField("LEVEL", SQLDataType.INTEGER.nullable(false), this, "");

    public final TableField<NodesRecord, Integer> SUCCESS
        = createField("SUCCESS", SQLDataType.INTEGER.nullable(false),
        this, "");

    public final TableField<NodesRecord, Integer> MATCHER_ID
        = createField("MATCHER_ID", SQLDataType.INTEGER.nullable(false),
        this, "");

    public final TableField<NodesRecord, Integer> START_INDEX
        = createField("START_INDEX", SQLDataType.INTEGER.nullable(false),
        this, "");

    public final TableField<NodesRecord, Integer> END_INDEX
        = createField("END_INDEX", SQLDataType.INTEGER.nullable(false), this,
        "");

    public final TableField<NodesRecord, Long> TIME
        = createField("TIME", SQLDataType.BIGINT.nullable(false), this, "");

    public Nodes()
    {
        this("NODES", null);
    }

    public Nodes(final String alias)
    {
        this(alias, NODES);
    }

    private Nodes(final String alias, final Table<NodesRecord> aliased)
    {
        this(alias, aliased, null);
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private Nodes(final String alias, final Table<NodesRecord> aliased,
        final Field<?>[] parameters)
    {
        super(alias, Public.PUBLIC, aliased, parameters, "");
    }

    @Override
    public UniqueKey<NodesRecord> getPrimaryKey()
    {
        return Keys.CONSTRAINT_47;
    }

    @Override
    public List<UniqueKey<NodesRecord>> getKeys()
    {
        return Arrays.<UniqueKey<NodesRecord>>asList(Keys.CONSTRAINT_47);
    }

    @Override
    public List<ForeignKey<NodesRecord, ?>> getReferences()
    {
        return Arrays.<ForeignKey<NodesRecord, ?>>asList(
            Keys.CONSTRAINT_470);
    }

    @Override
    public Nodes as(final String alias)
    {
        return new Nodes(alias, this);
    }

    public Nodes rename(final String name)
    {
        return new Nodes(name, null);
    }
}

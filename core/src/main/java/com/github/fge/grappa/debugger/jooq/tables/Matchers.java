package com.github.fge.grappa.debugger.jooq.tables;

import com.github.fge.grappa.debugger.jooq.Keys;
import com.github.fge.grappa.debugger.jooq.Public;
import com.github.fge.grappa.debugger.jooq.Tables;
import com.github.fge.grappa.debugger.jooq.tables.records.MatchersRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "PublicField", "ThisEscapedInObjectConstruction",
    "MethodCanBeVariableArityMethod" })
public class Matchers
    extends TableImpl<MatchersRecord>
{
    public static final Matchers MATCHERS = new Matchers();

    @Override
    public Class<MatchersRecord> getRecordType()
    {
        return MatchersRecord.class;
    }

    public final TableField<MatchersRecord, Integer> ID
        = createField("ID", SQLDataType.INTEGER.nullable(false), this, "");

    public final TableField<MatchersRecord, String> CLASS_NAME
        = createField("CLASS_NAME",
            SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    public final TableField<MatchersRecord, String> MATCHER_TYPE
        = createField("MATCHER_TYPE",
            SQLDataType.VARCHAR.length(30).nullable(false), this, "");

    public final TableField<MatchersRecord, String> NAME
        = createField("NAME", SQLDataType.VARCHAR.length(1024).nullable(false),
            this, "");

    public Matchers()
    {
        this("MATCHERS", null);
    }

    public Matchers(final String alias)
    {
        this(alias, Tables.MATCHERS);
    }

    private Matchers(final String alias, final Table<MatchersRecord> aliased)
    {
        this(alias, aliased, null);
    }

    private Matchers(final String alias, final Table<MatchersRecord> aliased,
        final Field<?>[] parameters)
    {
        super(alias, Public.PUBLIC, aliased, parameters, "");
    }

    @Override
    public UniqueKey<MatchersRecord> getPrimaryKey()
    {
        return Keys.CONSTRAINT_4;
    }

    @Override
    public List<UniqueKey<MatchersRecord>> getKeys()
    {
        return Arrays.<UniqueKey<MatchersRecord>>asList(Keys.CONSTRAINT_4);
    }

    @Override
    public Matchers as(final String alias)
    {
        return new Matchers(alias, this);
    }

    public Matchers rename(final String name)
    {
        return new Matchers(name, null);
    }
}

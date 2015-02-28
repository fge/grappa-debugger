package com.github.fge.grappa.debugger.jooq.tables.records;

import com.github.fge.grappa.debugger.jooq.tables.Matchers;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MatchersRecord
    extends UpdatableRecordImpl<MatchersRecord>
    implements Record4<Integer, String, String, String>
{
    public void setId(final Integer value)
    {
        setValue(0, value);
    }

    public Integer getId()
    {
        return (Integer) getValue(0);
    }

    public void setClassName(final String value)
    {
        setValue(1, value);
    }

    public String getClassName()
    {
        return (String) getValue(1);
    }

    public void setMatcherType(final String value)
    {
        setValue(2, value);
    }

    public String getMatcherType()
    {
        return (String) getValue(2);
    }

    public void setName(final String value)
    {
        setValue(3, value);
    }

    public String getName()
    {
        return (String) getValue(3);
    }

    @Override
    public Record1<Integer> key()
    {
        return (Record1) super.key();
    }

    @Override
    public Row4<Integer, String, String, String> fieldsRow()
    {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Integer, String, String, String> valuesRow()
    {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Integer> field1()
    {
        return Matchers.MATCHERS.ID;
    }

    @Override
    public Field<String> field2()
    {
        return Matchers.MATCHERS.CLASS_NAME;
    }

    @Override
    public Field<String> field3()
    {
        return Matchers.MATCHERS.MATCHER_TYPE;
    }

    @Override
    public Field<String> field4()
    {
        return Matchers.MATCHERS.NAME;
    }

    @Override
    public Integer value1()
    {
        return getId();
    }

    @Override
    public String value2()
    {
        return getClassName();
    }

    @Override
    public String value3()
    {
        return getMatcherType();
    }

    @Override
    public String value4()
    {
        return getName();
    }

    @Override
    public MatchersRecord value1(final Integer value)
    {
        setId(value);
        return this;
    }

    @Override
    public MatchersRecord value2(final String value)
    {
        setClassName(value);
        return this;
    }

    @Override
    public MatchersRecord value3(final String value)
    {
        setMatcherType(value);
        return this;
    }

    @Override
    public MatchersRecord value4(final String value)
    {
        setName(value);
        return this;
    }

    @Override
    public MatchersRecord values(final Integer value1, final String value2,
        final String value3, final String value4)
    {
        return this;
    }

    public MatchersRecord()
    {
        super(Matchers.MATCHERS);
    }

    public MatchersRecord(final Integer id, final String className,
        final String matcherType, final String name)
    {
        super(Matchers.MATCHERS);

        setValue(0, id);
        setValue(1, className);
        setValue(2, matcherType);
        setValue(3, name);
    }
}

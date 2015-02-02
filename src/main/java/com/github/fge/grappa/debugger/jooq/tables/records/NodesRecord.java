package com.github.fge.grappa.debugger.jooq.tables.records;

import com.github.fge.grappa.debugger.jooq.tables.Nodes;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NodesRecord
    extends UpdatableRecordImpl<NodesRecord>
    implements Record8<Integer, Integer, Integer, Integer, Integer, Integer,  Integer, Long>
{

    public void setId(final Integer value)
    {
        setValue(0, value);
    }

    public Integer getId()
    {
        return (Integer) getValue(0);
    }

    public void setParentId(final Integer value)
    {
        setValue(1, value);
    }

    public Integer getParentId()
    {
        return (Integer) getValue(1);
    }

    public void setLevel(final Integer value)
    {
        setValue(2, value);
    }

    public Integer getLevel()
    {
        return (Integer) getValue(2);
    }

    public void setSuccess(final Integer value)
    {
        setValue(3, value);
    }

    public Integer getSuccess()
    {
        return (Integer) getValue(3);
    }

    public void setMatcherId(final Integer value)
    {
        setValue(4, value);
    }

    public Integer getMatcherId()
    {
        return (Integer) getValue(4);
    }

    public void setStartIndex(final Integer value)
    {
        setValue(5, value);
    }

    public Integer getStartIndex()
    {
        return (Integer) getValue(5);
    }

    public void setEndIndex(final Integer value)
    {
        setValue(6, value);
    }

    public Integer getEndIndex()
    {
        return (Integer) getValue(6);
    }

    public void setTime(final Long value)
    {
        setValue(7, value);
    }

    public Long getTime()
    {
        return (Long) getValue(7);
    }

    @Override
    public Record1<Integer> key()
    {
        return (Record1) super.key();
    }

    @Override
    public Row8<Integer, Integer, Integer, Integer, Integer, Integer,
        Integer, Long> fieldsRow()
    {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Integer, Integer, Integer, Integer, Integer, Integer,
        Integer, Long> valuesRow()
    {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Integer> field1()
    {
        return Nodes.NODES.ID;
    }

    @Override
    public Field<Integer> field2()
    {
        return Nodes.NODES.PARENT_ID;
    }

    @Override
    public Field<Integer> field3()
    {
        return Nodes.NODES.LEVEL;
    }

    @Override
    public Field<Integer> field4()
    {
        return Nodes.NODES.SUCCESS;
    }

    @Override
    public Field<Integer> field5()
    {
        return Nodes.NODES.MATCHER_ID;
    }

    @Override
    public Field<Integer> field6()
    {
        return Nodes.NODES.START_INDEX;
    }

    @Override
    public Field<Integer> field7()
    {
        return Nodes.NODES.END_INDEX;
    }

    @Override
    public Field<Long> field8()
    {
        return Nodes.NODES.TIME;
    }

    @Override
    public Integer value1()
    {
        return getId();
    }

    @Override
    public Integer value2()
    {
        return getParentId();
    }

    @Override
    public Integer value3()
    {
        return getLevel();
    }

    @Override
    public Integer value4()
    {
        return getSuccess();
    }

    @Override
    public Integer value5()
    {
        return getMatcherId();
    }

    @Override
    public Integer value6()
    {
        return getStartIndex();
    }

    @Override
    public Integer value7()
    {
        return getEndIndex();
    }

    @Override
    public Long value8()
    {
        return getTime();
    }

    @Override
    public NodesRecord value1(final Integer value)
    {
        setId(value);
        return this;
    }

    @Override
    public NodesRecord value2(final Integer value)
    {
        setParentId(value);
        return this;
    }

    @Override
    public NodesRecord value3(final Integer value)
    {
        setLevel(value);
        return this;
    }

    @Override
    public NodesRecord value4(final Integer value)
    {
        setSuccess(value);
        return this;
    }

    @Override
    public NodesRecord value5(final Integer value)
    {
        setMatcherId(value);
        return this;
    }

    @Override
    public NodesRecord value6(final Integer value)
    {
        setStartIndex(value);
        return this;
    }

    @Override
    public NodesRecord value7(final Integer value)
    {
        setEndIndex(value);
        return this;
    }

    @Override
    public NodesRecord value8(final Long value)
    {
        setTime(value);
        return this;
    }

    @Override
    public NodesRecord values(final Integer value1, final Integer value2,
        final Integer value3, final Integer value4, final Integer value5,
        final Integer value6, final Integer value7, final Long value8)
    {
        return this;
    }

    public NodesRecord()
    {
        super(Nodes.NODES);
    }

    public NodesRecord(final Integer id, final Integer parentId,
        final Integer level, final Integer success, final Integer matcherId,
        final Integer startIndex, final Integer endIndex, final Long time)
    {
        super(Nodes.NODES);

        setValue(0, id);
        setValue(1, parentId);
        setValue(2, level);
        setValue(3, success);
        setValue(4, matcherId);
        setValue(5, startIndex);
        setValue(6, endIndex);
        setValue(7, time);
    }
}

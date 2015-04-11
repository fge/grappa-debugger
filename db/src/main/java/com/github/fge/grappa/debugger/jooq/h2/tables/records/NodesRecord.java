/**
 * This class is generated by jOOQ
 */
package com.github.fge.grappa.debugger.jooq.h2.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.4"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class NodesRecord extends org.jooq.impl.UpdatableRecordImpl<com.github.fge.grappa.debugger.jooq.h2.tables.records.NodesRecord> implements org.jooq.Record8<java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Long> {

	private static final long serialVersionUID = 1542958751;

	/**
	 * Setter for <code>PUBLIC.NODES.ID</code>.
	 */
	public void setId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>PUBLIC.NODES.ID</code>.
	 */
	public java.lang.Integer getId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>PUBLIC.NODES.PARENT_ID</code>.
	 */
	public void setParentId(java.lang.Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>PUBLIC.NODES.PARENT_ID</code>.
	 */
	public java.lang.Integer getParentId() {
		return (java.lang.Integer) getValue(1);
	}

	/**
	 * Setter for <code>PUBLIC.NODES.LEVEL</code>.
	 */
	public void setLevel(java.lang.Integer value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>PUBLIC.NODES.LEVEL</code>.
	 */
	public java.lang.Integer getLevel() {
		return (java.lang.Integer) getValue(2);
	}

	/**
	 * Setter for <code>PUBLIC.NODES.SUCCESS</code>.
	 */
	public void setSuccess(java.lang.Integer value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>PUBLIC.NODES.SUCCESS</code>.
	 */
	public java.lang.Integer getSuccess() {
		return (java.lang.Integer) getValue(3);
	}

	/**
	 * Setter for <code>PUBLIC.NODES.MATCHER_ID</code>.
	 */
	public void setMatcherId(java.lang.Integer value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>PUBLIC.NODES.MATCHER_ID</code>.
	 */
	public java.lang.Integer getMatcherId() {
		return (java.lang.Integer) getValue(4);
	}

	/**
	 * Setter for <code>PUBLIC.NODES.START_INDEX</code>.
	 */
	public void setStartIndex(java.lang.Integer value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>PUBLIC.NODES.START_INDEX</code>.
	 */
	public java.lang.Integer getStartIndex() {
		return (java.lang.Integer) getValue(5);
	}

	/**
	 * Setter for <code>PUBLIC.NODES.END_INDEX</code>.
	 */
	public void setEndIndex(java.lang.Integer value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>PUBLIC.NODES.END_INDEX</code>.
	 */
	public java.lang.Integer getEndIndex() {
		return (java.lang.Integer) getValue(6);
	}

	/**
	 * Setter for <code>PUBLIC.NODES.TIME</code>.
	 */
	public void setTime(java.lang.Long value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>PUBLIC.NODES.TIME</code>.
	 */
	public java.lang.Long getTime() {
		return (java.lang.Long) getValue(7);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Integer> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Long> fieldsRow() {
		return (org.jooq.Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Long> valuesRow() {
		return (org.jooq.Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field2() {
		return com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES.PARENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field3() {
		return com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES.LEVEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field4() {
		return com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES.SUCCESS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field5() {
		return com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES.MATCHER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field6() {
		return com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES.START_INDEX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field7() {
		return com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES.END_INDEX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field8() {
		return com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES.TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value2() {
		return getParentId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value3() {
		return getLevel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value4() {
		return getSuccess();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value5() {
		return getMatcherId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value6() {
		return getStartIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value7() {
		return getEndIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value8() {
		return getTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord value1(java.lang.Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord value2(java.lang.Integer value) {
		setParentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord value3(java.lang.Integer value) {
		setLevel(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord value4(java.lang.Integer value) {
		setSuccess(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord value5(java.lang.Integer value) {
		setMatcherId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord value6(java.lang.Integer value) {
		setStartIndex(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord value7(java.lang.Integer value) {
		setEndIndex(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord value8(java.lang.Long value) {
		setTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodesRecord values(java.lang.Integer value1, java.lang.Integer value2, java.lang.Integer value3, java.lang.Integer value4, java.lang.Integer value5, java.lang.Integer value6, java.lang.Integer value7, java.lang.Long value8) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached NodesRecord
	 */
	public NodesRecord() {
		super(com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES);
	}

	/**
	 * Create a detached, initialised NodesRecord
	 */
	public NodesRecord(java.lang.Integer id, java.lang.Integer parentId, java.lang.Integer level, java.lang.Integer success, java.lang.Integer matcherId, java.lang.Integer startIndex, java.lang.Integer endIndex, java.lang.Long time) {
		super(com.github.fge.grappa.debugger.jooq.h2.tables.Nodes.NODES);

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

/**
 * This class is generated by jOOQ
 */
package com.github.fge.grappa.debugger.postgresql.jooq;

import com.github.fge.grappa.debugger.postgresql.jooq.tables.Matchers;
import com.github.fge.grappa.debugger.postgresql.jooq.tables.Nodes;
import com.github.fge.grappa.debugger.postgresql.jooq.tables.ParseInfo;

/**
 * Convenience access to all tables in public
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.4"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

	/**
	 * The table public.matchers
	 */
	public static final Matchers MATCHERS = Matchers.MATCHERS;

	/**
	 * The table public.nodes
	 */
	public static final Nodes NODES = Nodes.NODES;

	/**
	 * The table public.parse_info
	 */
	public static final ParseInfo PARSE_INFO = ParseInfo.PARSE_INFO;
}
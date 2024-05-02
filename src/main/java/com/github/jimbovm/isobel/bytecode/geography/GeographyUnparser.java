package com.github.jimbovm.isobel.bytecode.geography;

import com.github.jimbovm.isobel.actor.PageSkip;
import com.github.jimbovm.isobel.actor.geography.GeographyActor;
import com.github.jimbovm.isobel.bytecode.common.BytecodeUnparser;
import com.github.jimbovm.isobel.common.AreaHeader;

/**
 * Implements an unparser for <code>GeographyActors</code>,
 * unparsing area geography data to game-readable bytecode.
 */
public final class GeographyUnparser extends BytecodeUnparser<GeographyActor> {

	AreaHeader header;

	/**
	 * Create a new unparser for an area with the supplied header.
	 * 
	 * @param header The header for the area.
	 */
	public GeographyUnparser(AreaHeader header) {
		super(GeographyParser.END_OF_FILE);
		this.header = header;
	}

	@Override
	protected byte[] unparsePageSkip(PageSkip skip, boolean newPage) {
		return DTypeCommand.unparse(skip, newPage);
	}

	@Override
	protected byte[] unparse(GeographyActor actor, boolean newPage) {
		return actor.unparse(newPage);
	}

	@Override
	protected byte[] unparseHeader() {
		return this.header.unparse();
	}
	
}

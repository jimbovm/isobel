package com.github.jimbovm.isobel.bytecode.population;

import com.github.jimbovm.isobel.actor.PageSkip;
import com.github.jimbovm.isobel.actor.population.PopulationActor;
import com.github.jimbovm.isobel.bytecode.common.BytecodeUnparser;
import com.github.jimbovm.isobel.common.Atlas;

/**
 * Implements an unparser for <code>PopulationActor</code>s,
 * unparsing area population data to game-readable bytecode.
 */
public final class PopulationUnparser extends BytecodeUnparser<PopulationActor> {

	private Atlas atlas;

	/**
	 * Create a new <code>PopulationUnparser</code> instance.
	 * 
	 * @param atlas An atlas from which to look up area IDs and index numbers.
	 */
	public PopulationUnparser(Atlas atlas) {
		super(PopulationParser.END_OF_FILE);
		this.atlas = atlas;
	}

	@Override
	protected byte[] unparsePageSkip(PageSkip skip, boolean newPage) {
		return PopulationCommand.unparse(skip, newPage);
	}

	@Override
	protected byte[] unparse(PopulationActor actor, boolean newPage) {
		return actor.unparse(newPage, this.atlas);
	}

	@Override
	protected byte[] unparseHeader() {
		return null;
	}
	
}

/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor;

/**
 * A design contract for the many actors that have an
 * "extent", including groups of blocks and extensible platforms.
 * 
 * An actor's extent refers to the number of units added to its base size.
 * For example, a pit with extent 0 will be one unit wide, with extent 1 it
 * will be two units wide, and so on. The direction in which an actor extends
 * is actor-dependent.
 */
public interface Extensible extends Actor {

	/** @return The actor's extent in blocks. */
	int getExtent();

	/**
	 * @param extent The actor's new extent in blocks.
	 */
	void setExtent(int extent);
}

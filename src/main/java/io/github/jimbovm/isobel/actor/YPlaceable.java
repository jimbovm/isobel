/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor;

/**
 * A design contract for an actor which is freely placeable in the Y axis
 * relative to the current page.
 *
 * As with the X axis, Y coordinates are absolute (counting from the start of
 * the level) rather than relative (counting from the left side of the current
 * page).
 */
public interface YPlaceable extends Actor {

	/**
	 * @return The actor's absolute Y position in blocks from the top of
	 *         the screen.
	 */
	public int getY();

	/**
	 * @param y The actor's new absolute Y position in blocks from the top of
	 *          the screen.
	 */
	public void setY(int y);

}

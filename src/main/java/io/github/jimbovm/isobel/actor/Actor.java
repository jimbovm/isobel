/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor;

/**
 * Parent class for all actors represented by geography and population
 * commands.
 *
 * Isobel represents coordinates as absolute (counting from the start
 * of the area) rather than relative (counting from the left side of
 * the current page).
 */
public interface Actor extends Comparable<Actor> {

	/**
	 * Return the actor's absolute X position in blocks from the start of the
	 * area.
	 * 
	 * @return The actor's absolute X position.
	 */
	public int getX();

	/**
	 * Set the actor's absolute X position in blocks from the start of the
	 * area.
	 *
	 * @param x The absolute X position
	 */
	public void setX(int x);

	/**
	 * Compare an actor to another actor. The comparison is based on the
	 * actor's absolute X position. It is useful to be able to sort actors
	 * based on where they appear in an area; however, as multiple actors
	 * can have the same X position, <strong>this and derived classes have a
	 * natural ordering that is inconsistent with
	 * <code>equals</code></strong>.
	 *
	 * @param  actor An actor against which to perform a comparison.
	 *
	 * @return       -1 if this actor has a lesser X position than the argument, 1
	 *               if the argument's X position is lesser, and 0 otherwise.
	 */
	default int compareTo(Actor actor) {
		if (actor == null) {
			throw new NullPointerException("Invalid comparison of actor with null");
		}
		if (this.getX() < actor.getX()) {
			return -1;
		}
		else if (this.getX() > actor.getX()) {
			return 1;
		}
		return 0;
	}
}

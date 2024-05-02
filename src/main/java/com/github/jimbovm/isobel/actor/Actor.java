/* SPDX-License-Identifier: MIT-0

Copyright 2022-2024 Jimbo Brierley.

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal in 
the Software without restriction, including without limitation the rights to 
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
of the Software, and to permit persons to whom the Software is furnished to do 
so.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
SOFTWARE. */

package com.github.jimbovm.isobel.actor;

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
	 * @param actor An actor against which to perform a comparison.
	 *
	 * @return -1 if this actor has a lesser X position than the argument, 1
	 * if the argument's X position is lesser, and 0 otherwise. 
	 */
	default int compareTo(Actor actor) {
		if (actor == null) {
			throw new NullPointerException(
				"Invalid comparison of actor with null"
				);
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

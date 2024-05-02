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
	 * the screen. */
	public int getY();

	/** @param y The actor's new absolute Y position in blocks from the top of
	 * the screen. */
	public void setY(int y);

}

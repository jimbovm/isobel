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

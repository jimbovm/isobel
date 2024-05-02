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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a page skip actor. When loaded in, this actor instructs
 * the game not to read any more data until the page counter
 * reaches the target page, "locking in" the current level
 * configuration as set by geography actors until that point.
 *
 * This class is not parsed <em>from</em> bytecode, as Isobel converts all
 * coordinates into absolute positions for ease of manipulation. It is
 * only generated as part of unparsing <em>to</em> bytecode.
 */
@Getter
@Setter
@ToString
public final class PageSkip implements Actor {
	
	/** The X position of the page setter. */
	private int x;
	/** The value to which to set the page counter. */
	private int target;
}

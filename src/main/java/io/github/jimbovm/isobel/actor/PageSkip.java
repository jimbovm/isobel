/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor;

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

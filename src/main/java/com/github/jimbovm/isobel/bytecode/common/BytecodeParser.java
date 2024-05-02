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

package com.github.jimbovm.isobel.bytecode.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import com.github.jimbovm.isobel.actor.Actor;

/**
 * This generalised parser class factors out common, reusable
 * functionality for parsing both kinds of raw SMB level bytecode
 * (geography and population).
 *
 * @see com.github.jimbovm.isobel.bytecode.population.PopulationParser
 * @see com.github.jimbovm.isobel.bytecode.geography.GeographyParser
 */
@Getter
@Setter
@Log4j2
public abstract class BytecodeParser<T extends Actor> {

	/** What is returned at the end of a stream. */
	protected final int END_OF_STREAM = -1;

	/** The current value of the page counter. */
	protected int page = 0;

	/** The input stream which is to be read. */
	protected InputStream source;

	/** The number of bytes read so far. */
	protected int bytesRead;

	/**
	 * Test whether the new page flag is set on a byte.
	 * 
	 * @param theByte A piece of bytecode.
	 * @return True if the input has the new page flag set, false otherwise.
	 */
	protected boolean newPageFlagSet(final int theByte) {
		return ((theByte & 0b10000000) >>> 7) == 1;
	}

	/**
	 * Create a new parser object.
	 * 
	 * @param source The stream from which the parser should read.
	 */
	public BytecodeParser(InputStream source) {
		this.source = source;
	}

	/**
	 * Test whether a byte begins a three-byte command.
	 * 
	 * @param lowByte The first byte of a bytecode command.
	 * @return True if the byte begins a three-byte command, false otherwise.
	 */
	protected abstract boolean isThreeByte(final int lowByte);

	/**
	 * Test whether a byte begins a page skip command.
	 * 
	 * @param lowByte The first byte of a bytecode command.
	 * @param highByte The second byte of a bytecode command.
	 * @return True if the byte begins a page skip command, false otherwise.
	 */
	protected abstract boolean isPageSkip(final int lowByte, final int highByte);

	/**
	 * Handle a three-byte command.
	 * 
	 * @param lowByte The first byte of the command.
	 * @param midByte The second byte of the command.
	 * @param highByte The third byte of the command.
	 * 
	 * @return An object representative of the parameters.
	 */
	protected abstract T handleThreeByte(final int lowByte, final int midByte, final int highByte);

	/**
	 * Handle a two-byte command.
	 * 
	 * @param lowByte The first byte of the command.
	 * @param highByte The second byte of the command.
	 * 
	 * @return An object representative of the parameters.
	 */
	protected abstract T handleTwoByte(final int lowByte, final int highByte);

	/**
	 * Read bytes from an {@link InputStream}, two or three per iteration,
	 * deducing whether they are part of two- or three-byte commands (or the
	 * single-byte end marker) from the first byte per iteration read, and
	 * handling accordingly. Three-byte detection may be achieved via
	 * dependency injection.
	 *
	 * @param endMarker The single-byte sentinel indicating end of data.
	 * @throws IOException in the event of an issue with the input stream.
	 *
	 * @return A {@link List} of objects representative of the bytes in the
	 * input stream.
	 */
	protected List<@NotNull T> parse(final int endMarker) throws IOException {

		List<T> actors = new LinkedList<>();

		int lowByte;
		int midByte;
		int highByte;

		boolean endOfData = false;
		boolean endOfStream = false;

		for (;;) {

			lowByte = source.read();
			bytesRead++;

			endOfData = (lowByte == endMarker);
			endOfStream = (lowByte == this.END_OF_STREAM);

			if (endOfData) {
				// We read the end marker, exit loop successfully
				bytesRead++;
				log.info(String.format("End marker 0x%x read, %d bytes total", endMarker, bytesRead));
				break;
			}
			
			if (endOfStream) {
				// This shouldn't happen; we should always read the end marker
				throw new IOException(
					String.format(
						"Malformed data at byte %d; byte array must end with end marker (0x%x).",
						bytesRead, endMarker)); 
			}

			/* If we reach this point, we're good to continue.
			 * Is this a three-byte command? */
			if (this.isThreeByte(lowByte) == true) {
				// Yes, it's three bytes
				midByte = this.source.read();
				highByte = this.source.read();
				bytesRead += 2;

				if ((midByte == endMarker) || (highByte == endMarker)) {
					// Premature end of data
					throw new IOException(
						String.format(
							"Malformed data at byte %d; read end marker (0x%x) while reading three-byte command",
							bytesRead, endMarker));
				}

				if (this.newPageFlagSet(midByte)) {
					this.page++;
				}

				T threeByteActor = handleThreeByte(lowByte, midByte, highByte);
				actors.add(threeByteActor);

			}
			else {
				// No, it's two bytes
				highByte = this.source.read();
				bytesRead++;
				if (highByte == endMarker) {throw new IOException(
					String.format(
						"Malformed data at byte %d; read end marker (0x%x) while reading two-byte command",
						bytesRead, endMarker));
				}

				if (this.newPageFlagSet(highByte)) {
					this.page++;
				}

				/* Do we have a page skip? If so, set the page, and
				continue the loop without adding an actor. */
				if (isPageSkip(lowByte, highByte)) {
					final int newPage = (highByte & 0b00111111);
					this.setPage(newPage);
				} else {
					T twoByteActor = handleTwoByte(lowByte, highByte);
					actors.add(twoByteActor);
				}
			}
		}

		return actors;
	}
}

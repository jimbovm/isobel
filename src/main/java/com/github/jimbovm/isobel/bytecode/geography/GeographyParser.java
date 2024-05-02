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

package com.github.jimbovm.isobel.bytecode.geography;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.jimbovm.isobel.actor.geography.GeographyActor;
import com.github.jimbovm.isobel.bytecode.common.BytecodeParser;
import com.github.jimbovm.isobel.common.AreaHeader;

import lombok.extern.log4j.Log4j2;

/**
 * Parses an input stream of bytes, interpreting them as geography commands and
 * outputting a list of actor objects representing what those commands spawn
 * in-game.
 */
@Log4j2
public final class GeographyParser extends BytecodeParser<GeographyActor> {

	/** The EOF marker for geography data. */
	public static final int END_OF_FILE = 0xFD;

	/** 
	 * Construct a new parser with the given input source.
	 * @param	source	The stream from which to read the input.
	*/
	public GeographyParser(InputStream source) {
		super(source);
	}

	/**
	 * Parse an area header, represented by the first two bytes of a
	 * valid stream of geography data. 
	 * 
	 * @return An area header object parsed from the stream. 
	 */
	public AreaHeader parseHeader() {

		AreaHeader header;

		try {
			// Read the two header bytes first
			final int lowHeaderByte = this.source.read();
			final int highHeaderByte = this.source.read();

			// Fail on end of stream
			if (lowHeaderByte == this.END_OF_STREAM || highHeaderByte == this.END_OF_STREAM) {
				throw new IOException("End of stream while reading header");
			}

			// Set the header
			header = AreaHeader.parse(lowHeaderByte, highHeaderByte);
			// Reset the stream to the start
			this.source.reset();
		}
		catch (IOException e) {
			System.err.print(e.getMessage());
			return null;
		}

		return header;
	}

	protected GeographyActor handleTwoByte(final int lowByte, final int highByte) {
		return this.parseCommand(lowByte, highByte);
	}

	/** 
	 * Not used, always returns null. There are no 3-byte geography commands.
	 * 
	 * @param lowByte The first byte of the command.
	 * @param midByte The second byte of the command.
	 * @param highByte The third byte of the command.
	 * @return <code>null</code>
	 */
	protected GeographyActor handleThreeByte(final int lowByte, final int midByte, final int highByte) {
		return null;
	}
	
	private GeographyActor parseCommand(final int lowByte, final int highByte) {

		log.info(String.format(
			"Parsing geography command from bytes %02x %02x (%s %s)", 
			lowByte, highByte,
			StringUtils.leftPad(Integer.toBinaryString(lowByte), 8, '0'),
			StringUtils.leftPad(Integer.toBinaryString(highByte), 8, '0')));
	
		int commandType = lowByte & 0x0F;
	
		switch (commandType) {
			case 0xC: return CTypeCommand.parse(lowByte, highByte, page);
			case 0xD: return DTypeCommand.parse(lowByte, highByte, page);
			case 0xE: return ETypeCommand.parse(lowByte, highByte, page);
			case 0xF: return FTypeCommand.parse(lowByte, highByte, page);
			default: return NormalCommand.parse(lowByte, highByte, page);
		}
	}

	/**
	 * Parse geography actors from the parser's input stream. This method
	 * ignores the first two bytes, which are the area header and should be
	 * interpreted separately with {@link parseHeader}.
	 * 
	 * @return A list of geography actors parsed from the 
	 * {@link InputStream} given at construction.
	 * @throws IOException In the event of a problem with the input stream.
	 */
	public List<GeographyActor> parse() throws IOException {
		// skip over the header bytes
		this.source.skip(2);
		List<GeographyActor> parsed = super.parse(END_OF_FILE);
		this.source.reset();
		return parsed;
	}

	/** Always returns false. There are no 3-byte geography commands. */
	@Override
	protected boolean isThreeByte(int lowByte) {
		return false;
	}

	/**
	 * Returns true if the high nybble of the low byte is 0xD and bit
	 * 6 of the high byte is zero, and false otherwise.
	 */
	@Override
	protected boolean isPageSkip(int lowByte, int highByte) {
		/* D-type commands are page setters when the 7th bit of the
		high byte is _CLEAR_, and are _NOT_ page setters when it is
		_SET_! The Bonimy docs are _WRONG_ about this! */
		return ((lowByte & 0xF) == 0xD) && (((highByte & 0b01000000) >>> 6) == 0);
	}
}

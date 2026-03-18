/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.test;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.TypedArgumentConverter;

public class BinaryIntegerConverter extends TypedArgumentConverter<String, Integer> {

	protected BinaryIntegerConverter() {
		super(String.class, Integer.class);
	}

	@Override
	protected Integer convert(String source) throws ArgumentConversionException {
		try {
			if (source != null && source.startsWith("0b")) {
				return Integer.parseInt(source.substring(2).replaceAll("_", ""), 2);
			}
			else {
				throw new NumberFormatException();
			}
		}
		catch (NumberFormatException e) {
			throw new ArgumentConversionException(
				String.format("Unable to convert %s to an integer; incorrect number format", source));
		}
	}

}

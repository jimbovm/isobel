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

package com.github.jimbovm.isobel.test;

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
		} catch (NumberFormatException e) {
			throw new ArgumentConversionException(
				String.format(
					"Unable to convert %s to an integer; incorrect number format",
					source)
				);
		}
	}
			
}

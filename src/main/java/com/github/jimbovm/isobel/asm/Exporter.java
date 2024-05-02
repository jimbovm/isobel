/*
SPDX-License-Identifier: MIT-0

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
SOFTWARE.
*/

package com.github.jimbovm.isobel.asm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Encapsulates functionality for writing generated assembly out to a file.
 */
public final class Exporter {
	
	/**
	 * Write a bundle of generated assembly to files whose names are given by the 
	 * keys of the {@link Map} passed in.
	 * 
	 * @param bundle A {@link Map} of filenames to their contents (generated assembly).
	 */
	public static void export(Map<String, String> bundle) {
		
		for (String filename : bundle.keySet()) {

			File output = new File(filename + ".asm");

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
				
				writer.append(bundle.get(filename));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

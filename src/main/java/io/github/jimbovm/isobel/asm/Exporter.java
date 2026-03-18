/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.asm;

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
	 * @param bundle A {@link Map} of filenames to their contents (generated
	 *               assembly).
	 */
	public static void export(Map<String, String> bundle) {

		for (String filename : bundle.keySet()) {

			File output = new File(filename + ".asm");

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {

				writer.append(bundle.get(filename));

			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TestSuite {

	private static Logger log = LogManager.getLogger();

	public Path getFileFromResources(String filename) {

		File file = new File(this.getClass().getClassLoader().getResource(filename).getFile());
		Path path = file.toPath();

		try {
			String canonicalPath = URLDecoder.decode(path.toString(), "UTF-8");
			return Path.of(canonicalPath);
		}
		catch (UnsupportedEncodingException e) {
			log.error("Could not decode path using UTF-8. This should never happen.");
		}

		return null;
	}

	public byte[] readGeoFile(String filename) throws IOException {

		Path path = getFileFromResources(filename);
		return Files.readAllBytes(path);
	}

}

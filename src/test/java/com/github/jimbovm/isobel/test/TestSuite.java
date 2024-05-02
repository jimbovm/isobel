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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Files;

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

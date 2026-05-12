/*
 * Copyright (C) 2001-2002  Zaval Creative Engineering Group (http://www.zaval.org)
 * Copyright (C) 2026 Christoph Obexer <cobexer@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * (version 2) as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.zaval.tools.i18n.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BundleManagerTest {
	private final BundleManager manager = new BundleManager();

	@Test
	void baseNameStripsDirectoryAndExtension() {
		assertEquals("Messages", manager.baseName("/path/to/Messages.properties"));
	}

	@Test
	void baseNameWithNoDirectory() {
		assertEquals("Messages", manager.baseName("Messages.properties"));
	}

	@Test
	void baseNameWithNoExtension() {
		assertEquals("Messages", manager.baseName("Messages"));
	}

	@Test
	void baseNameWithWindowsPath() {
		assertEquals("Messages", manager.baseName("C:\\path\\to\\Messages.properties"));
	}

	@Test
	void determineLanguageForBaseFileReturnsEn() {
		assertEquals("en", manager.determineLanguage("Messages.properties"));
	}

	@Test
	void determineLanguageForSingleSuffix() {
		assertEquals("de", manager.determineLanguage("Messages_de.properties"));
	}

	@Test
	void determineLanguageForCountrySuffix() {
		assertEquals("de_DE", manager.determineLanguage("Messages_de_DE.properties"));
	}

	@Test
	void replaceSubstitutesSingleOccurrence() {
		assertEquals("a/b/c", manager.replace("a\\b\\c", "\\", "/"));
	}

	@Test
	void replaceSubstitutesAllOccurrences() {
		assertEquals("x-x-x", manager.replace("xaxax", "a", "-"));
	}

	@Test
	void replaceReturnsOriginalWhenNoMatch() {
		String original = "no match here";
		assertEquals(original, manager.replace(original, "xyz", "ABC"));
	}

	@Test
	void replaceHandlesEmptyString() {
		assertEquals("", manager.replace("", "a", "b"));
	}

	@Test
	void appendResourceParsesPropertiesFromStream() throws IOException {
		String content = "greeting=Hello\nfarewell=Goodbye\n";
		ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.ISO_8859_1));
		manager.appendResource(stream, "en");

		BundleSet bundle = manager.getBundle();
		assertNotNull(bundle.getItem("greeting"));
		assertEquals("Hello", bundle.getItem("greeting").getTranslation("en"));
		assertEquals("Goodbye", bundle.getItem("farewell").getTranslation("en"));
	}

	@Test
	void appendResourceIgnoresEmptyLines() throws IOException {
		String content = "key1=value1\n\nkey2=value2\n";
		manager.appendResource(new ByteArrayInputStream(content.getBytes(StandardCharsets.ISO_8859_1)), "en");
		assertEquals(2, manager.getBundle().getItemCount());
	}

	@Test
	void appendResourceParsesComments() throws IOException {
		String content = "#a comment\nkey=value\n";
		manager.appendResource(new ByteArrayInputStream(content.getBytes(StandardCharsets.ISO_8859_1)), "en");
		assertEquals("a comment", manager.getBundle().getItem("key").getComment());
	}

	@Test
	void appendResourceHandlesEmptyValue() throws IOException {
		String content = "key=\n";
		manager.appendResource(new ByteArrayInputStream(content.getBytes(StandardCharsets.ISO_8859_1)), "en");
		assertEquals("", manager.getBundle().getItem("key").getTranslation("en"));
	}

	@Test
	void storeWritesPropertiesFile(@TempDir Path tmpDir) throws IOException {
		manager.appendResource(new ByteArrayInputStream("greeting=Hello\n".getBytes(StandardCharsets.ISO_8859_1)), "en");
		String targetFile = tmpDir.resolve("output.properties").toString();
		manager.store(targetFile);

		assertTrue(Files.exists(Path.of(targetFile)));
		String stored = Files.readString(Path.of(targetFile));
		assertTrue(stored.contains("greeting=Hello"));
	}

	@Test
	void newBundleManagerHasEmptyBundle() {
		assertTrue(manager.getBundle().getItems().findAny().isEmpty());
		assertNull(manager.getBundle().getFirstLanguage());
	}
}

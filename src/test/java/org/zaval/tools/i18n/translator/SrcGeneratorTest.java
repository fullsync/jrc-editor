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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SrcGeneratorTest {
	@Test
	void performGeneratesClassWithKeyMethods(@TempDir Path tmpDir) throws IOException {
		Path outFile = tmpDir.resolve("MyBundle.java");
		BundleSet set = new BundleSet();
		set.addLanguage("en");
		set.addKey("app.title").setTranslation("en", "Title");
		set.addKey("app.cancel").setTranslation("en", "Cancel");

		new SrcGenerator(outFile.toString()).perform(set);

		String src = Files.readString(outFile);
		assertTrue(src.contains("class MyBundle"));
		assertTrue(src.contains("getAppTitle"));
		assertTrue(src.contains("setAppTitle"));
		assertTrue(src.contains("getAppCancel"));
		assertTrue(src.contains("setAppCancel"));
	}

	@Test
	void performGeneratesLoadFromResourceMethod(@TempDir Path tmpDir) throws IOException {
		Path outFile = tmpDir.resolve("Res.java");
		BundleSet set = new BundleSet();
		set.addLanguage("en");
		set.addKey("label").setTranslation("en", "Label");

		new SrcGenerator(outFile.toString()).perform(set);

		String src = Files.readString(outFile);
		assertTrue(src.contains("loadFromResource"));
		assertTrue(src.contains("ResourceBundle"));
	}

	@Test
	void performHandlesTopLevelKeyWithNoDots(@TempDir Path tmpDir) throws IOException {
		Path outFile = tmpDir.resolve("Simple.java");
		BundleSet set = new BundleSet();
		set.addLanguage("en");
		set.addKey("title").setTranslation("en", "My App");

		new SrcGenerator(outFile.toString()).perform(set);

		String src = Files.readString(outFile);
		assertTrue(src.contains("getTitle"));
		assertTrue(src.contains("setTitle"));
	}
}

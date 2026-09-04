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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BundleSetTest {
	private BundleSet set;

	@BeforeEach
	void setUp() {
		set = new BundleSet();
	}

	@Test
	void newSetHasNoLanguages() {
		assertFalse(set.hasLanguages());
		assertEquals(0, set.getLanguageCount());
	}

	@Test
	void addLanguageCreatesEntry() {
		set.addLanguage("en");
		assertTrue(set.hasLanguages());
		assertEquals(1, set.getLanguageCount());
		assertNotNull(set.getLanguage("en"));
	}

	@Test
	void addLanguageTwiceIsIdempotent() {
		set.addLanguage("en");
		set.addLanguage("en");
		assertEquals(1, set.getLanguageCount());
	}

	@Test
	void addLanguageSetsLocaleDescription() {
		set.addLanguage("de");
		LangItem lang = set.getLanguage("de");
		assertNotNull(lang);
		assertEquals("de", lang.getId());
		assertTrue(lang.getDescription().toLowerCase().contains("german") || lang.getDescription().toLowerCase().contains("deutsch"));
	}

	@Test
	void addLanguageWithCountrySetsCompoundDescription() {
		set.addLanguage("de_DE");
		LangItem lang = set.getLanguage("de_DE");
		assertNotNull(lang);
		assertTrue(lang.getDescription().contains("("));
	}

	@Test
	void getFirstLanguageReturnsNullWhenEmpty() {
		assertNull(set.getFirstLanguage());
	}

	@Test
	void getFirstLanguageReturnsFirstAdded() {
		set.addLanguage("en");
		set.addLanguage("de");
		assertEquals("en", set.getFirstLanguage().getId());
	}

	@Test
	void getLanguagesReturnsAllAdded() {
		set.addLanguage("en");
		set.addLanguage("fr");
		LangItem[] langs = set.getLanguages();
		assertEquals(2, langs.length);
	}

	@Test
	void getLanguageIndexReturnsMinusOneForMissing() {
		set.addLanguage("en");
		assertEquals(-1, set.getLanguageIndex("de"));
	}

	@Test
	void getLanguageIndexForFirstLanguageIsZero() {
		set.addLanguage("en");
		assertEquals(0, set.getLanguageIndex("en"));
	}

	@Test
	void addKeyCreatesItem() {
		BundleItem item = set.addKey("app.title");
		assertNotNull(item);
		assertEquals("app.title", item.getId());
		assertEquals(1, set.getItemCount());
	}

	@Test
	void addKeyIsIdempotent() {
		BundleItem first = set.addKey("app.title");
		BundleItem second = set.addKey("app.title");
		assertEquals(first, second);
		assertEquals(1, set.getItemCount());
	}

	@Test
	void getItemReturnsNullForMissingKey() {
		assertNull(set.getItem("missing"));
	}

	@Test
	void getItemReturnsAddedKey() {
		set.addKey("key1");
		assertNotNull(set.getItem("key1"));
	}

	@Test
	void removeKeyDeletesItem() {
		set.addKey("key1");
		set.removeKey("key1");
		assertNull(set.getItem("key1"));
		assertEquals(0, set.getItemCount());
	}

	@Test
	void getItemsStreamCoversAllKeys() {
		set.addKey("b");
		set.addKey("a");
		List<String> ids = set.getItems().map(BundleItem::getId).collect(Collectors.toList());
		assertEquals(2, ids.size());
		assertTrue(ids.contains("a"));
		assertTrue(ids.contains("b"));
	}

	@Test
	void getKeysBeginningWithFiltersCorrectly() {
		set.addKey("app.title");
		set.addKey("app.cancel");
		set.addKey("menu.file");
		List<BundleItem> appKeys = set.getKeysBeginningWith("app.").collect(Collectors.toList());
		assertEquals(2, appKeys.size());
	}

	@Test
	void removeKeysBeginningWithRemovesMatchingKeys() {
		set.addKey("app.title");
		set.addKey("app.cancel");
		set.addKey("menu.file");
		set.removeKeysBeginningWith("app.");
		assertNull(set.getItem("app.title"));
		assertNull(set.getItem("app.cancel"));
		assertNotNull(set.getItem("menu.file"));
	}

	@Test
	void updateValueSetsTranslationOnExistingItem() {
		set.addKey("key1");
		set.addLanguage("en");
		set.updateValue("key1", "en", "Hello");
		assertEquals("Hello", set.getItem("key1").getTranslation("en"));
	}

	@Test
	void updateValueDoesNothingForMissingKey() {
		set.updateValue("missing", "en", "value");
	}

	@Test
	void storeProducesKeyValueLines() {
		set.addLanguage("en");
		BundleItem item = set.addKey("greeting");
		item.setTranslation("en", "Hello");
		List<String> lines = set.store("en");
		assertTrue(lines.contains("greeting=Hello"));
	}

	@Test
	void storeSkipsItemsWithNullTranslation() {
		set.addLanguage("en");
		set.addLanguage("de");
		BundleItem item = set.addKey("greeting");
		item.setTranslation("en", "Hello");
		List<String> lines = set.store("de");
		assertTrue(lines.isEmpty());
	}

	@Test
	void storeIncludesCommentBeforeKey() {
		set.addLanguage("en");
		BundleItem item = set.addKey("key1");
		item.setComment("a comment");
		item.setTranslation("en", "value");
		List<String> lines = set.store("en");
		int commentIdx = lines.indexOf("#a comment");
		int valueIdx = lines.indexOf("key1=value");
		assertTrue(commentIdx >= 0);
		assertTrue(valueIdx >= 0);
		assertTrue(commentIdx < valueIdx);
	}
}

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BundleItemTest {
	@Test
	void constructorSetsId() {
		BundleItem item = new BundleItem("my.key");
		assertEquals("my.key", item.getId());
	}

	@Test
	void getTranslationReturnsNullForUnknownLanguage() {
		BundleItem item = new BundleItem("key");
		assertNull(item.getTranslation("en"));
	}

	@Test
	void setAndGetTranslationRoundtrip() {
		BundleItem item = new BundleItem("key");
		item.setTranslation("en", "Hello");
		item.setTranslation("de", "Hallo");
		assertEquals("Hello", item.getTranslation("en"));
		assertEquals("Hallo", item.getTranslation("de"));
	}

	@Test
	void setTranslationOverwritesPreviousValue() {
		BundleItem item = new BundleItem("key");
		item.setTranslation("en", "Old");
		item.setTranslation("en", "New");
		assertEquals("New", item.getTranslation("en"));
	}

	@Test
	void commentIsNullByDefault() {
		BundleItem item = new BundleItem("key");
		assertNull(item.getComment());
	}

	@Test
	void setAndGetComment() {
		BundleItem item = new BundleItem("key");
		item.setComment("a comment");
		assertEquals("a comment", item.getComment());
	}

	@Test
	void getLanguagesReflectsSetTranslations() {
		BundleItem item = new BundleItem("key");
		item.setTranslation("en", "Hello");
		item.setTranslation("fr", "Bonjour");
		assertTrue(item.getLanguages().contains("en"));
		assertTrue(item.getLanguages().contains("fr"));
		assertEquals(2, item.getLanguages().size());
	}

	@Test
	void getLanguagesIsEmptyWhenNoTranslationsSet() {
		BundleItem item = new BundleItem("key");
		assertTrue(item.getLanguages().isEmpty());
	}
}

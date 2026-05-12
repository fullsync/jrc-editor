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

import org.junit.jupiter.api.Test;

class LangItemTest {
	@Test
	void constructorSetsIdAndDescription() {
		LangItem item = new LangItem("de", "German");
		assertEquals("de", item.getId());
		assertEquals("German", item.getDescription());
	}

	@Test
	void fileNameIsNullByDefault() {
		LangItem item = new LangItem("en", "English");
		assertNull(item.getFileName());
	}

	@Test
	void setAndGetFileName() {
		LangItem item = new LangItem("en", "English");
		item.setFileName("messages_en.properties");
		assertEquals("messages_en.properties", item.getFileName());
	}

	@Test
	void setFileNameOverwritesPreviousValue() {
		LangItem item = new LangItem("en", "English");
		item.setFileName("old.properties");
		item.setFileName("new.properties");
		assertEquals("new.properties", item.getFileName());
	}
}

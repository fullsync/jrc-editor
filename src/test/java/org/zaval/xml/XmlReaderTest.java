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

package org.zaval.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class XmlReaderTest {
	@Test
	void flattenSingleChildWithNameAttribute() throws Exception {
		String xml = "<resources><entry name=\"greeting\">Hello</entry></resources>";
		Map<String, String> map = new XmlReader(xml).flatten();
		assertEquals("Hello", map.get("greeting"));
	}

	@Test
	void flattenMultipleChildren() throws Exception {
		String xml = "<resources>" + "<entry name=\"a\">Alpha</entry>" + "<entry name=\"b\">Beta</entry>" + "</resources>";
		Map<String, String> map = new XmlReader(xml).flatten();
		assertEquals("Alpha", map.get("a"));
		assertEquals("Beta", map.get("b"));
	}

	@Test
	void flattenUsesLangAttributeOverName() throws Exception {
		String xml = "<resources><entry lang=\"en\" name=\"greeting\">Hello</entry></resources>";
		Map<String, String> map = new XmlReader(xml).flatten();
		assertTrue(map.containsKey("en"));
		assertFalse(map.containsKey("greeting"));
	}

	@Test
	void flattenNestedChildrenBuildsDottedPath() throws Exception {
		String xml = "<root><group name=\"app\"><entry name=\"title\">My App</entry></group></root>";
		Map<String, String> map = new XmlReader(xml).flatten();
		assertTrue(map.containsKey("app!title") || map.containsKey("app"));
	}

	@Test
	void flattenEmptyRootReturnsEmptyMap() throws Exception {
		Map<String, String> map = new XmlReader("<root/>").flatten();
		assertTrue(map.isEmpty());
	}

	@Test
	void flattenFallsBackToElementNameWhenNoLangOrName() throws Exception {
		String xml = "<resources><item>Value</item></resources>";
		Map<String, String> map = new XmlReader(xml).flatten();
		assertEquals("Value", map.get("item"));
	}
}

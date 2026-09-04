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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

class XmlElementTest {
	private XmlElement parse(String xml) throws IOException, XmlParseException {
		XmlElement el = new XmlElement();
		el.parse(new StringReader(xml));
		return el;
	}

	@Test
	void parseSimpleElement() throws Exception {
		XmlElement el = parse("<root/>");
		assertEquals("root", el.getName());
	}

	@Test
	void parseElementWithContent() throws Exception {
		XmlElement el = parse("<message>Hello World</message>");
		assertEquals("message", el.getName());
		assertEquals("Hello World", el.getContent());
	}

	@Test
	void parseAttribute() throws Exception {
		XmlElement el = parse("<item name=\"foo\"/>");
		assertEquals("foo", el.getAttribute("name"));
	}

	@Test
	void attributeNamesAreLowercased() throws Exception {
		XmlElement el = parse("<item Name=\"bar\"/>");
		assertEquals("bar", el.getAttribute("name"));
	}

	@Test
	void parseChildElements() throws Exception {
		XmlElement el = parse("<root><child1/><child2/></root>");
		assertEquals(2, el.children().size());
		assertEquals("child1", el.children().get(0).getName());
		assertEquals("child2", el.children().get(1).getName());
	}

	@Test
	void parseCdataSection() throws Exception {
		XmlElement el = parse("<root><![CDATA[<not-a-tag>]]></root>");
		assertTrue(el.getContent().contains("<not-a-tag>"));
	}

	@Test
	void parseEntityReferences() throws Exception {
		XmlElement el = parse("<root>&amp;&lt;&gt;&quot;&apos;</root>");
		assertEquals("&<>\"'", el.getContent());
	}

	@Test
	void parseNumericCharacterReference() throws Exception {
		XmlElement el = parse("<root>&#65;</root>");
		assertEquals("A", el.getContent());
	}

	@Test
	void parseHexCharacterReference() throws Exception {
		XmlElement el = parse("<root>&#x41;</root>");
		assertEquals("A", el.getContent());
	}

	@Test
	void parseXmlDeclarationIsSkipped() throws Exception {
		XmlElement el = parse("<?xml version=\"1.0\"?><root/>");
		assertEquals("root", el.getName());
	}

	@Test
	void malformedXmlThrowsXmlParseException() {
		assertThrows(XmlParseException.class, () -> parse("<unclosed"));
	}

	@Test
	void unknownEntityThrowsXmlParseException() {
		assertThrows(XmlParseException.class, () -> parse("<root>&unknown;</root>"));
	}

	@Test
	void toStringProducesXml() throws Exception {
		XmlElement el = parse("<item key=\"k\">value</item>");
		String result = el.toString();
		assertNotNull(result);
		assertTrue(result.contains("item"));
		assertTrue(result.contains("value"));
	}

	@Test
	void parseMultipleAttributesOnElement() throws Exception {
		XmlElement el = parse("<entry lang=\"en\" name=\"greeting\">Hello</entry>");
		assertEquals("en", el.getAttribute("lang"));
		assertEquals("greeting", el.getAttribute("name"));
		assertEquals("Hello", el.getContent());
	}
}

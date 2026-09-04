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

package org.zaval.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Locale;

import org.junit.jupiter.api.Test;

class SafeResourceBundleTest {
	private static final String FAILURE_STRING = "?????";

	@Test
	void getStringReturnsFallbackForMissingBundle() {
		SafeResourceBundle bundle = new SafeResourceBundle("nonexistent.resource.bundle", null);
		assertEquals(FAILURE_STRING, bundle.getString("any.key"));
	}

	@Test
	void getStringReturnsFallbackForMissingKey() {
		SafeResourceBundle bundle = new SafeResourceBundle("jrc-editor", Locale.ENGLISH);
		assertEquals(FAILURE_STRING, bundle.getString("this.key.does.not.exist"));
	}

	@Test
	void getStringReturnsValueForExistingKey() {
		SafeResourceBundle bundle = new SafeResourceBundle("jrc-editor", Locale.ROOT);
		String value = bundle.getString("dialog.button.cancel");
		assertEquals("Cancel", value);
	}

	@Test
	void nullLocaleReturnsNonFallbackValueForExistingKey() {
		SafeResourceBundle bundle = new SafeResourceBundle("jrc-editor", null);
		String value = bundle.getString("dialog.button.cancel");
		assertFalse(FAILURE_STRING.equals(value), "Expected a real translation, not the failure string");
	}
}

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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class LambdaUtilsTest {
	@Test
	void uncheckedSupplierReturnsValue() {
		Supplier<String> supplier = LambdaUtils.unchecked(() -> "hello");
		assertEquals("hello", supplier.get());
	}

	@Test
	void uncheckedSupplierPropagatesCheckedException() {
		Supplier<String> supplier = LambdaUtils.unchecked(() -> {
			throw new IOException("test error");
		});
		assertThrows(IOException.class, supplier::get);
	}

	@Test
	void uncheckedConsumerRunsSuccessfully() {
		int[] counter = { 0 };
		Consumer<String> consumer = LambdaUtils.unchecked(s -> counter[0]++);
		consumer.accept("anything");
		assertEquals(1, counter[0]);
	}

	@Test
	void uncheckedConsumerPropagatesCheckedException() {
		Consumer<String> consumer = LambdaUtils.unchecked(s -> {
			throw new IOException("consumer error");
		});
		assertThrows(IOException.class, () -> consumer.accept("input"));
	}

	@Test
	void uncheckedSupplierWithRuntimeExceptionPropagatesDirectly() {
		Supplier<String> supplier = LambdaUtils.unchecked(() -> {
			throw new IllegalStateException("runtime");
		});
		assertThrows(IllegalStateException.class, supplier::get);
	}
}

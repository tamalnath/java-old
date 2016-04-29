package org.tamal.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Tamal Kanti Nath
 */
@SuppressWarnings("static-method")
public class MathTest {

	/**
	 * This method proves expressions are evaluated in left-to-right order.
	 */
	@Test
	public void testLeftToRight() {
		int i;
		int j = (i = 10) * i;
		assertEquals(10, i);
		assertEquals(10 * 10, j);

		i = 10;
		j = i = 2 * i;
		assertEquals(i, j);

		i = 10;
		j = i + (i = 1);
		assertEquals(1, i);
		assertEquals(11, j);

		i = 10;
		i += i = 1;
		assertEquals(11, i);

		i = 10;
		Exception e = null;
		try {
			j = (i / 0) / (i = 1);
		} catch (ArithmeticException ae) {
			e = ae;
			assertEquals(10, i);
		}
		assertNotNull(e);

		i = Integer.MAX_VALUE;
		j = (i * 2) / 2;
		assertNotEquals(i, j);
	}

	/**
	 * This method proves unary operators are evaluated before binary operators.
	 */
	@Test
	public void testUnary() {
		int i = 10;
		int j = i++ + ++i + i++ + ++i;
		assertEquals(10 + 12 + 12 + 14, j);
		j = --i - i-- - --i - i--;
		assertEquals(13 - 13 - 11 - 11, j);
	}

	/**
	 * This test proves that parameters in a method call are evaluated from left to right.
	 */
	@Test
	public void testParameterEvaluationOrder() {
		int i = 10;
		String str = String.format("%d %d %d %d", i++, ++i, i++, ++i);
		assertEquals("10 12 12 14", str);
		i = 10;
		str = String.format("%d %d %d %d", ++i, i++, ++i, i++);
		assertEquals("11 11 13 13", str);
	}

	/**
	 * This test proves that array dimensions are evaluated from left to right.
	 * However, in an array access, the expression to the left of the brackets
	 * appears to be fully evaluated before any part of the expression within
	 * the brackets is evaluated.
	 */
	@Test
	public void testArrayEvaluationOrder() {
		int[][] array = { { 0, 1 }, { 2, 3 } };
		int i = 0;
		assertEquals(array[0][1], array[i++][i++]);
		assertEquals(array[1][0], array[--i][--i]);

		assertEquals(3, array[1][ (array[1] = array[0])[1] ]);
		assertEquals(array[0], array[1]);
	}

	/**
	 * This test proves NaN cannot be compared.
	 */
	@Test
	public void testNaN() {
		double d = 0.0d;
		assertFalse(Double.NaN < d);
		assertFalse(Double.NaN <= d);
		assertFalse(Double.NaN > d);
		assertFalse(Double.NaN >= d);
		assertFalse(Double.NaN == d);
		assertTrue(Double.NaN != d);
		double NaN = 0.0d/0.0;
		assertTrue(Double.isNaN(NaN));
		assertFalse(Double.NaN == NaN);
	}

	/**
	 * This test proves arithmetic overflow do not throw any error.
	 */
	@Test
	public void testOverflow() {
		int i = Integer.MAX_VALUE;
		assertEquals(0b0111_1111_1111_1111_1111_1111_1111_1111, i);
		i = i + 1;
		assertEquals(0b1000_0000_0000_0000_0000_0000_0000_0000, i);
		assertEquals(Integer.MIN_VALUE, i);
		i = 0;
		assertEquals(0b0000_0000_0000_0000_0000_0000_0000_0000, i);
		i = i - 1;
		assertEquals(0b1111_1111_1111_1111_1111_1111_1111_1111, i);
	}

}

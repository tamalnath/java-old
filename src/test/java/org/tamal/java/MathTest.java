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
		double x = 0.0d;
		double y = -0.0d/0.0;
		assertFalse(x != x);
		assertTrue(y != y);
		assertTrue(Double.isNaN(y));
		assertFalse(Double.NaN == y);
		assertFalse(x < y);
		assertFalse(x <= y);
		assertFalse(x > y);
		assertFalse(x >= y);
		assertFalse(x == y);
		assertTrue(x != y);
		assertFalse((x < y) == !(x >= y));
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

	/**
	 * This test demonstrates the IEEE 754 single precision floating point conversion.
	 */
	@Test
	public void testFloat() {
		float f = Float.MAX_VALUE;
		int i = 0b0_11111110_11111111111111111111111;
		assertEquals(i, Float.floatToIntBits(f));

		f=Float.POSITIVE_INFINITY;
		i = 0b0_11111111_00000000000000000000000;
		assertEquals(i, Float.floatToIntBits(f));

		i = 0b0_11111111_00000000000000000000001;
		f = Float.intBitsToFloat(i);
		assertTrue(Float.isNaN(f));
		assertFalse(f == Float.NaN);

		i = 0b0_11111111_10000000000000000000000;
		f = Float.intBitsToFloat(i);
		assertTrue(Float.isNaN(f));
		assertEquals(i, Float.floatToIntBits(Float.NaN));

		i = 0b0_11111111_11111111111111111111111;
		f = Float.intBitsToFloat(i);
		assertTrue(Float.isNaN(f));

		f = Float.MIN_VALUE;
		i = 0b0_00000000_00000000000000000000001;
		assertEquals(i, Float.floatToIntBits(f));

		f = 0.0F;
		i = 0b0_00000000_00000000000000000000000;
		assertEquals(i, Float.floatToIntBits(f));

		f = -0.0F;
		i = 0b1_00000000_00000000000000000000000;
		assertEquals(i, Float.floatToIntBits(f));

		assertFalse( 0.0F > -0.0F);
		assertFalse( 0.0F < -0.0F);
		assertEquals(1, Float.compare(0.0f, -0.0f));
	}

}

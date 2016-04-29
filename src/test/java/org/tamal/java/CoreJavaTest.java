package org.tamal.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

/**
 * @author Tamal Kanti Nath
 */
@SuppressWarnings("static-method")
public class CoreJavaTest {

	private static void staticMethod() {
		// Empty
	}

	/**
	 * This method proves instanceof operator yields false for null.
	 */
	@Test
	public void testClone() {
		Date date1 = new Date();
		Date date2 = (Date) date1.clone();
		assertTrue(date1.equals(date2));
		assertFalse(date1 == date2);
	}

	/**
	 * This method proves instanceof operator yields false for null.
	 * @throws CloneNotSupportedException as NotClonable do not implement {@link Cloneable} interface
	 */
	@Test(expected = CloneNotSupportedException.class)
	public void testCloneNotSupportedException() throws CloneNotSupportedException {
		NotClonable nc1 = new NotClonable();
		nc1.clone();
	}

	/**
	 * This method proves instanceof operator yields false for null.
	 */
	@Test
	@SuppressWarnings({ "cast", "null" })
	public void testInstanceof() {
		String s = null;
		assertFalse(s instanceof Object);
		s = "";
		assertTrue(s instanceof Object);
	}

	/**
	 * This test proves that integers between -128 and 127 (inclusive) are cached as required by JLS.
	 * The same caching works for Long (-128 to 127), Short (-128 to 127), Character (0 to 127), Byte (-128 to 127) and Boolean.
	 */
	@Test
	public void testIntegerCache() {
		Integer i = -129;
		Integer j = -129;
		assertNotSame("Did not expect same object", i, j);
		i = -128;
		j = -128;
		assertSame("Expected same object", i, j);
		i = 127;
		j = 127;
		assertSame("Expected same object", i, j);
		i = 128;
		j = 128;
		assertNotSame("Did not expect same object", i, j);
	}

	/**
	 * This test proves that String literals are cached by compiler.
	 */
	@Test
	public void testStringCache() {
		String s1 = "string";
		String s2 = "str" + "ing";
		assertSame("Expected same object", s1, s2);
		String ing = new String("ing");
		s1 = "str" + ing;
		s2 = "str" + ing;
		assertNotSame("Did not expect same object", s1, s2);
		s1 = s1.intern();
		s2 = s2.intern();
		assertSame("Expected same object", s1, s2);
	}

	/**
	 * This test proves that getClass method returns runtime class of the object.
	 */
	@Test
	public void testGetClass() {
		Number n = 0;
		Class<? extends Number> c = n.getClass();
		assertEquals("Expected Integer", Integer.class, c);
	}

	/**
	 * This test demonstrates valid identifiers and literals.
	 */
	@Test
	@SuppressWarnings("unused")
	public void testIdentifierLiterals() {
		// Below literals are valid:
		Object String, Class, αρετη, $1, _1;

		// Below literals are invalid:
		// Object 1number, class, null, false;

		// Below are the valid numeric literals
		int[] ints = { -111, -0x1f, -017, -0b10 };
		float[] floats = { -11.11f, -11E-11f, 11.f, .11f };
		double[] doubles = { -1_1.1_1, -1_1E-1_1 };
		char[] chars = { 'a', '\t', '\\', '\'', '\uFFFF', '\377', '™' };

		// Below unicode escaped characters cannot appear:
		// line feed (LF or \n): \u000a
		// carriage return (CR or \r): \u000d
		// double quotation (\"): \u0022
	}

	/**
	 * This test demonstrates how two array should be compared.
	 */
	@Test
	public void testArrayEquals() {
		int []arr1 = {1, 2, 3, 4, 5};
		int []arr2 = {1, 2, 3, 4, 5};
		assertFalse(arr1 == arr2);
		assertFalse(arr1.equals(arr2));
		assertTrue(Arrays.equals(arr1, arr2));
	}

	/**
	 * This test proves that static methods can be called by class instance variables which is null.
	 */
	@Test
	@SuppressWarnings("static-access")
	public void testStaticMethod() {
		CoreJavaTest cc = null;
		cc.staticMethod();
	}

	private class NotClonable {

		public NotClonable() {
			// Empty
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
	}

}

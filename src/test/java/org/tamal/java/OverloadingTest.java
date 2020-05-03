package org.tamal.java;


import org.testng.annotations.Test;

import java.io.Serializable;

import static org.testng.Assert.assertEquals;


/**
 * @author Tamal Kanti Nath
 */
@SuppressWarnings({"static-method", "unused"})
public class OverloadingTest {

	/**
	 * This test proves overloading precedence for byte is byte, short, int, long, float, double.
	 */
	@Test
	public void byteOverloading() {
		byte n = Byte.MAX_VALUE;
		assertEquals("byte", overloadByte(n));
		assertEquals("short", overloadShort(n));
		assertEquals("int", overloadInt(n));
		assertEquals("long", overloadLong(n));
		assertEquals("float", overloadFloat(n));
		assertEquals("double", overloadDouble(n));
	}

	/**
	 * This test proves overloading precedence for short is short, int, long, float, double.
	 */
	@Test
	public void shortOverloading() {
		short n = Short.MAX_VALUE;
		assertEquals("short", overloadShort(n));
		assertEquals("int", overloadInt(n));
		assertEquals("long", overloadLong(n));
		assertEquals("float", overloadFloat(n));
		assertEquals("double", overloadDouble(n));
	}

	/**
	 * This test proves overloading precedence for int is int, long, float, double.
	 */
	@Test
	public void intOverloading() {
		int n = Integer.MAX_VALUE;
		assertEquals("int", overloadInt(n));
		assertEquals("long", overloadLong(n));
		assertEquals("float", overloadFloat(n));
		assertEquals("double", overloadDouble(n));
	}

	/**
	 * This test proves overloading precedence for long is long, float, double.
	 */
	@Test
	public void longOverloading() {
		long n = Long.MAX_VALUE;
		assertEquals("long", overloadLong(n));
		assertEquals("float", overloadFloat(n));
		assertEquals("double", overloadDouble(n));
	}

	/**
	 * This test proves overloading precedence for float is float, double.
	 */
	@Test
	public void floatOverloading() {
		float n = Float.MAX_VALUE;
		assertEquals("float", overloadFloat(n));
		assertEquals("double", overloadDouble(n));
	}

	/**
	 * This test proves overloading precedence for double is double.
	 */
	@Test
	public void doubleOverloading() {
		double n = Double.MAX_VALUE;
		assertEquals("double", overloadDouble(n));
	}

	private static String overloadByte(byte n) {
		return byte.class.getSimpleName();
	}

	private static String overloadByte(short n) {
		return short.class.getSimpleName();
	}

	private static String overloadByte(int n) {
		return int.class.getSimpleName();
	}

	private static String overloadByte(long n) {
		return long.class.getSimpleName();
	}

	private static String overloadByte(float n) {
		return float.class.getSimpleName();
	}

	private static String overloadByte(double n) {
		return float.class.getSimpleName();
	}

	private static String overloadShort(short n) {
		return short.class.getSimpleName();
	}

	private static String overloadShort(int n) {
		return int.class.getSimpleName();
	}

	private static String overloadShort(long n) {
		return long.class.getSimpleName();
	}

	private static String overloadShort(float n) {
		return float.class.getSimpleName();
	}

	private static String overloadShort(double n) {
		return float.class.getSimpleName();
	}

	private static String overloadInt(int n) {
		return int.class.getSimpleName();
	}

	private static String overloadInt(long n) {
		return long.class.getSimpleName();
	}

	private static String overloadInt(float n) {
		return float.class.getSimpleName();
	}

	private static String overloadInt(double n) {
		return float.class.getSimpleName();
	}

	private static String overloadLong(long n) {
		return long.class.getSimpleName();
	}

	private static String overloadLong(float n) {
		return float.class.getSimpleName();
	}

	private static String overloadLong(double n) {
		return float.class.getSimpleName();
	}

	private static String overloadFloat(float n) {
		return float.class.getSimpleName();
	}

	private static String overloadFloat(double n) {
		return float.class.getSimpleName();
	}

	private static String overloadDouble(double n) {
		return double.class.getSimpleName();
	}

	/**
	 * This test proves that overloading precedence are: widening, wrapper, var-args, widening with varargs. 
	 * But widening and wrapper cannot go together.
	 */
	@Test
	public void wideningWrapperVarargs() {
		int n = Integer.MAX_VALUE;
		assertEquals("long", overloadWidening(n));
		assertEquals("Integer", overloadWrapper(n));
		assertEquals("int[]", overloadVarargs(n));
		assertEquals("long[]", overloadWideningVarargs(n));
	}

	private static String overloadWidening(long n) {
		return long.class.getSimpleName();
	}

	private static String overloadWidening(Integer n) {
		return Integer.class.getSimpleName();
	}

	private static String overloadWidening(int... n) {
		return int[].class.getSimpleName();
	}

	private static String overloadWidening(long... n) {
		return long[].class.getSimpleName();
	}

	private static String overloadWrapper(Integer n) {
		return Integer.class.getSimpleName();
	}

	private static String overloadWrapper(int... n) {
		return int[].class.getSimpleName();
	}

	private static String overloadWrapper(long... n) {
		return long[].class.getSimpleName();
	}

	private static String overloadVarargs(int... n) {
		return int[].class.getSimpleName();
	}

	private static String overloadVarargs(long... n) {
		return long[].class.getSimpleName();
	}

	private static String overloadWideningVarargs(long... n) {
		return long[].class.getSimpleName();
	}

	/**
	 * This test proves that overloaded method obeys class hierarchy.
	 */
	@Test
	public void overloadingHierarchy() {
		assertEquals("Integer", overloadInteger(0));
		assertEquals("Number", overloadNumber(0));
		assertEquals("Object", overloadObject(0));
	}

	private static String overloadInteger(Integer n) {
		return Integer.class.getSimpleName();
	}

	private static String overloadInteger(Number n) {
		return Number.class.getSimpleName();
	}

	private static String overloadInteger(Object n) {
		return Object.class.getSimpleName();
	}

	private static String overloadNumber(Number n) {
		return Number.class.getSimpleName();
	}

	private static String overloadNumber(Object n) {
		return Object.class.getSimpleName();
	}

	private static String overloadObject(Object n) {
		return Object.class.getSimpleName();
	}


	/**
	 * This test proves that overloaded method obeys class hierarchy.
	 */
	@Test
	public void overloadingAmbiguity() {
		/*
		 * Integer implements both Serializable (by extending Number) and Comparable interface.
		 * It creates ambiguity as those interfaces have same precedence.
		 */
		// assertEquals("Serializable or Comparable", overloadSerializableComparable(0));
		assertEquals("Serializable", overloadSerializableComparable((Serializable) 0));
		assertEquals("Comparable", overloadSerializableComparable((Comparable<Integer>) 0));

		/*
		 * Integer varargs and int varargs are ambiguous.
		 */
		// assertEquals("int[] or Integer[]", overloadIntInteger(0));
		// assertEquals("int[] or Integer[]", overloadIntInteger(Integer.valueOf(0)));
		assertEquals("int[]", overloadIntInteger(new int[] {0, 1}));
		assertEquals("Integer[]", overloadIntInteger(new Integer[] {0, 1}));
	}

	private static String overloadSerializableComparable(Serializable i) {
		return Serializable.class.getSimpleName();
	}

	private static String overloadSerializableComparable(Comparable<Integer> i) {
		return Comparable.class.getSimpleName();
	}

	private static String overloadIntInteger(int... i) {
		return int[].class.getSimpleName();
	}

	private static String overloadIntInteger(Integer... i) {
		return Integer[].class.getSimpleName();
	}

}

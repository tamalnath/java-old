package org.tamal.java;


import org.testng.annotations.Test;

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Tamal Kanti Nath
 */
public class GenericsTest {

	List<Number> numbers;
	List<? extends Number> extendsNumber;
	List<? super Number> superNumber;

	TypedClass<?, ?> t = new TypedClass<>();

	/**
	 *
	 */
	@Test
	public void testGenerics() {
		TypeVariable<?>[] typeVariables = t.getClass().getTypeParameters();
		assertEquals(2, typeVariables.length);
		assertEquals("I1", typeVariables[0].getName());
		assertEquals("I2", typeVariables[1].getName());

		Type[] types = typeVariables[0].getBounds();
		assertEquals(2, types.length);
		assertEquals(Serializable.class, types[0]);
		assertEquals(Externalizable.class, types[1]);

		types = typeVariables[1].getBounds();
		assertEquals(1, types.length);
		assertTrue(types[0] instanceof ParameterizedType);

		ParameterizedType parameterizedType = (ParameterizedType) types[0];
		assertEquals(Comparable.class, parameterizedType.getRawType());

		types = parameterizedType.getActualTypeArguments();
		assertEquals(1, types.length);
		assertTrue(types[0] instanceof WildcardType);

		WildcardType wildcardType = (WildcardType) types[0];
		types = wildcardType.getLowerBounds();
		assertEquals(1, types.length);
		assertEquals(Number.class, types[0]);

		types = wildcardType.getUpperBounds();
		assertEquals(1, types.length);
		assertEquals(Object.class, types[0]);

	}

	class TypedClass<I1 extends Serializable & Externalizable, I2 extends Comparable<? super Number>> {
		// Empty
	}

	/**
	 * This test proves:
	 */
	@Test
	public void testWildcardType() {
		numbers = new ArrayList<>(); // new ArrayList<Number>();
		// numbers = new ArrayList<Integer>(); // Type mismatch

		extendsNumber = new ArrayList<>(); // new ArrayList<Number>();
		extendsNumber = new ArrayList<Integer>();
		// extendsNumber = new ArrayList<Object>(); // Type mismatch

		superNumber = new ArrayList<>(); // new ArrayList<Number>();
		// superNumber = new ArrayList<Integer>(); // Type mismatch
		superNumber = new ArrayList<Object>();

		numbers.add(0);
		// extendsNumber.add(0); // argument mismatch
		superNumber.add(0);

		numbers = Arrays.asList(BigDecimal.ZERO, BigInteger.ONE);
		extendsNumber = Arrays.asList(BigDecimal.ZERO, BigInteger.ONE);
		superNumber = Arrays.asList(BigDecimal.ZERO, BigInteger.ONE);

		Collections.copy(superNumber, extendsNumber);
		Collections.copy(superNumber, numbers);
		Collections.copy(numbers, extendsNumber);
		Collections.copy(numbers, extendsNumber);
	}

}

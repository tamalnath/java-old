package org.tamal.java;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

/**
 * This test suite demonstrates the usage of Annotation.
 * @author Tamal Kanti Nath
 */
@Foo("class")
public class AnnotationTest {

	private @Foo("i1") @Foo("i2") int i;
	private @Foo("a1") int @Foo("a2") [] @Foo("a3") [] @Foo("a4") [] @Foo("a5") [] array; // annotates array
	private @Foo("l1") List<@Foo("l2") List<@Foo("l3") List<@Foo("l4") String>>> list; // annotates a list

	/**
	 * Initialize test.
	 */
	@Foo("constructor")
	public AnnotationTest() {
		// Empty
	}

	@Foo("method")
	private void method(@Foo("param") int param) throws @Foo("exception") Exception {
		i = param;
	}

	/**
	 * This test demonstrates the usage of class annotations.
	 * @throws SecurityException should not happen
	 */
	@Test
	public void testClassAnnotation() {
		Foo foo = getClass().getAnnotation(Foo.class);
		assertEquals("class", foo.value()[0]);
	}

	/**
	 * This test demonstrates the usage of annotation annotations.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void testAnnotationAnnotation() {
		Foo foo = FooList.class.getAnnotation(Foo.class);
		assertEquals("annotation", foo.value()[0]);
	}

	/**
	 * This test demonstrates the usage of package annotations.
	 */
	@Test
	public void testPackageAnnotation() {
		Foo foo = getClass().getPackage().getAnnotation(Foo.class);
		assertEquals("package", foo.value()[0]);
	}

	/**
	 * This test demonstrates the usage of method annotations.
	 * @throws NoSuchMethodException should not happen
	 * @throws SecurityException should not happen
	 */
	@Test
	public void testConstructorAnnotation() throws NoSuchMethodException, SecurityException {
		Foo foo = getClass().getConstructor().getDeclaredAnnotation(Foo.class);
		assertEquals("constructor", foo.value()[0]);
	}

	/**
	 * This test demonstrates the usage of method annotations.
	 * @throws NoSuchMethodException should not happen
	 * @throws SecurityException should not happen
	 */
	@Test
	public void testMethodAnnotation() throws NoSuchMethodException, SecurityException {
		Method m = getClass().getDeclaredMethod("method", int.class);
		Foo foo = m.getDeclaredAnnotation(Foo.class);
		assertEquals("method", foo.value()[0]);

		foo = m.getParameters()[0].getDeclaredAnnotation(Foo.class);
		assertEquals("param", foo.value()[0]);

		foo = m.getAnnotatedExceptionTypes()[0].getAnnotation(Foo.class);
		assertEquals("exception", foo.value()[0]);

	}

	/**
	 * This test demonstrates the usage of repeatable annotations.
	 * @throws NoSuchFieldException should not happen
	 * @throws SecurityException should not happen
	 */
	@Test
	public void testRepeatableAnnotation() throws NoSuchFieldException, SecurityException {
		Field f = getClass().getDeclaredField("i");
		FooList fooList = f.getAnnotation(FooList.class);
		Foo[] foos = fooList.value();
		assertEquals(2, foos.length);
		assertEquals("i1", foos[0].value()[0]);
		assertEquals("i2", foos[1].value()[0]);

		foos = f.getAnnotationsByType(Foo.class);
		assertEquals(2, foos.length);
		assertEquals("i1", foos[0].value()[0]);
		assertEquals("i2", foos[1].value()[0]);
	}

	/**
	 * This test demonstrates the usage of multi-dimensional array annotations.
	 * @throws NoSuchFieldException should not happen
	 * @throws SecurityException should not happen
	 */
	@Test
	public void testArrayAnnotation() throws NoSuchFieldException, SecurityException {
		Field f = getClass().getDeclaredField("array");
		Foo foo = f.getDeclaredAnnotation(Foo.class);
		assertEquals("a1", foo.value()[0]);
		AnnotatedArrayType aat = (AnnotatedArrayType) f.getAnnotatedType();
		foo = aat.getAnnotation(Foo.class);
		assertEquals("a2", foo.value()[0]);
		aat = (AnnotatedArrayType) aat.getAnnotatedGenericComponentType();
		foo = aat.getDeclaredAnnotation(Foo.class);
		assertEquals("a3", foo.value()[0]);
		aat = (AnnotatedArrayType) aat.getAnnotatedGenericComponentType();
		foo = aat.getDeclaredAnnotation(Foo.class);
		assertEquals("a4", foo.value()[0]);
		aat = (AnnotatedArrayType) aat.getAnnotatedGenericComponentType();
		foo = aat.getDeclaredAnnotation(Foo.class);
		assertEquals("a5", foo.value()[0]);
	}

	/**
	 * This test demonstrates the usage of multi-level type annotations.
	 * @throws NoSuchFieldException should not happen
	 * @throws SecurityException should not happen
	 */
	@Test
	public void testTypeAnnotation() throws NoSuchFieldException, SecurityException {
		Field f = getClass().getDeclaredField("list");
		Foo foo = f.getDeclaredAnnotation(Foo.class);
		assertEquals("l1", foo.value()[0]);
		AnnotatedParameterizedType apt = (AnnotatedParameterizedType) f.getAnnotatedType();
		foo = apt.getDeclaredAnnotation(Foo.class);
		assertEquals("l1", foo.value()[0]);
		apt = (AnnotatedParameterizedType) apt.getAnnotatedActualTypeArguments()[0];
		foo = apt.getDeclaredAnnotation(Foo.class);
		assertEquals("l2", foo.value()[0]);
		apt = (AnnotatedParameterizedType) apt.getAnnotatedActualTypeArguments()[0];
		foo = apt.getDeclaredAnnotation(Foo.class);
		assertEquals("l3", foo.value()[0]);
		AnnotatedType at = apt.getAnnotatedActualTypeArguments()[0];
		foo = at.getDeclaredAnnotation(Foo.class);
		assertEquals("l4", foo.value()[0]);
	}

}

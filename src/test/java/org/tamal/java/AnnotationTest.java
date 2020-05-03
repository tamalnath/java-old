package org.tamal.java;

import org.testng.annotations.Test;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * This test suite demonstrates the usage of Annotation.
 *
 * @author Tamal Kanti Nath
 */
@Foo("class")
public class AnnotationTest {

	private final @Foo("i1") @Foo("i2") int i;
	private final @Foo("a1") int @Foo("a2") [] @Foo("a3") [] array = null; // annotates array
	private final @Foo("l1") List<@Foo("l2") List<@Foo("l3") String>> list = null; // annotates a list

	@Foo("constructor")
	public AnnotationTest() {
		i = -1;
	}

	@Foo("method")
	private void method(@Foo("param") int param) throws @Foo("exception") Exception {
		throw new Exception();
	}

	/**
	 * This test demonstrates the usage of class annotations.
	 */
	@Test
	public void testClassAnnotation() {
		Foo foo = getClass().getAnnotation(Foo.class);
		assertEquals(foo.value()[0], "class");
	}

	/**
	 * This test demonstrates the usage of annotation annotations.
	 */
	@Test
	public void testAnnotationAnnotation() {
		Foo foo = Foo.class.getAnnotation(Foo.class);
		assertEquals(foo.value()[0], "annotation");
	}

	/**
	 * This test demonstrates the usage of package annotations.
	 */
	@Test
	public void testPackageAnnotation() {
		Foo foo = getClass().getPackage().getAnnotation(Foo.class);
		assertEquals(foo.value()[0], "package");
	}

	/**
	 * This test demonstrates the usage of method annotations.
	 * @throws NoSuchMethodException should not happen
	 * @throws SecurityException should not happen
	 */
	@Test
	public void testConstructorAnnotation() throws NoSuchMethodException, SecurityException {
		Foo foo = getClass().getConstructor().getDeclaredAnnotation(Foo.class);
		assertEquals(foo.value()[0], "constructor");
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
		assertEquals(foo.value()[0], "method");

		foo = m.getParameters()[0].getDeclaredAnnotation(Foo.class);
		assertEquals(foo.value()[0], "param");

		foo = m.getAnnotatedExceptionTypes()[0].getAnnotation(Foo.class);
		assertEquals(foo.value()[0], "exception");

	}

	/**
	 * This test demonstrates the usage of repeatable annotations.
	 * @throws NoSuchFieldException should not happen
	 * @throws SecurityException should not happen
	 */
	@Test
	public void testRepeatableAnnotation() throws NoSuchFieldException, SecurityException {
		Field f = getClass().getDeclaredField("i");
		Foo[] foos = f.getAnnotation(Foo.List.class).value();
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
		assertEquals(foo.value()[0], "a1");
		AnnotatedArrayType aat = (AnnotatedArrayType) f.getAnnotatedType();
		foo = aat.getAnnotation(Foo.class);
		assertEquals(foo.value()[0], "a2");
		aat = (AnnotatedArrayType) aat.getAnnotatedGenericComponentType();
		foo = aat.getDeclaredAnnotation(Foo.class);
		assertEquals(foo.value()[0], "a3");
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
		assertEquals(foo.value()[0], "l1");
		AnnotatedParameterizedType apt = (AnnotatedParameterizedType) f.getAnnotatedType();
		foo = apt.getDeclaredAnnotation(Foo.class);
		assertEquals(foo.value()[0], "l1");
		apt = (AnnotatedParameterizedType) apt.getAnnotatedActualTypeArguments()[0];
		foo = apt.getDeclaredAnnotation(Foo.class);
		assertEquals(foo.value()[0], "l2");
		AnnotatedType at = apt.getAnnotatedActualTypeArguments()[0];
		foo = at.getDeclaredAnnotation(Foo.class);
		assertEquals(foo.value()[0], "l3");
	}

}

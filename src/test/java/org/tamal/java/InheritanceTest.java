package org.tamal.java;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Tamal Kanti Nath
 */
@SuppressWarnings({ "static-access" })
public class InheritanceTest {

	/**
	 * This test proves that a static methods and static fields are resolved at
	 * compile time. But a non-static methods and non-static fields are
	 * resolved at runtime. It also proves that static method can be called by
	 * null instance variables.
	 */
	@Test
	public void compileTimeVsRuntime() {
		Class1 class1 = null;
		Class2 class2 = null;
		Class3 class3 = null;

		assertEquals(class1.staticMethod(), "Class1");
		assertEquals(class2.staticMethod(), "Class2");
		assertEquals(class3.staticMethod(), "Class3");

		assertEquals(class1.staticVariable, "Class1");
		assertEquals(class2.staticVariable, "Class2");
		assertEquals(class3.staticVariable, "Class3");

		class1 = new Class1();
		class2 = new Class2();
		class3 = new Class3();

		assertEquals(class1.variable, "Class1");
		assertEquals(class2.variable, "Class2");
		assertEquals(class3.variable, "Class3");

		assertEquals(class1.method(), "Class1");
		assertEquals(class2.method(), "Class2");
		assertEquals(class3.method(), "Class3");

		assertEquals(((Interface1) class1).method(), "Class1");
		assertEquals(((Interface1) class2).method(), "Class2");
		assertEquals(((Interface1) class3).method(), "Class3");

		assertEquals(((Interface1) class1).variable, "Interface1.variable");
		assertEquals(((Interface1) class2).variable, "Interface1.variable");
		assertEquals(((Interface1) class3).variable, "Interface1.variable");

		class1 = new Class3();
		class2 = new Class3();
		class3 = new Class3();

		assertEquals(class1.variable, "Class3");
		assertEquals(class2.variable, "Class3");
		assertEquals(class3.variable, "Class3");

		assertEquals(class1.method(), "Class3");
		assertEquals(class2.method(), "Class3");
		assertEquals(class3.method(), "Class3");

		assertEquals(((Interface2) class1).method(), "Class3");
		assertEquals(((Interface2) class2).method(), "Class3");
		assertEquals(((Interface2) class3).method(), "Class3");

		assertEquals(((Interface2) class1).variable, "Interface2.variable");
		assertEquals(((Interface2) class2).variable, "Interface2.variable");
		assertEquals(((Interface2) class3).variable, "Interface2.variable");

	}

	private interface Interface1 {
		String variable = "Interface1.variable";
		default String method() {
			return "Interface1.defaultMethod";
		}
	}

	private interface Interface2 {
		String variable = "Interface2.variable";
		default String method() {
			return "Interface2.defaultMethod";
		}
	}

	private static class Class1 implements Interface1, Interface2 {
		static String staticVariable = Class1.class.getSimpleName();
		String variable = getClass().getSimpleName();

		/**
		 * Although this method is declared as a default method in both the
		 * interfaces, we need to override this method to avoid ambiguity.
		 */
		@Override
		public String method() {
			return variable;
		}

		static String staticMethod() {
			return staticVariable;
		}

	}

	private static class Class2 extends Class1 {
		static String staticVariable = Class2.class.getSimpleName();
		String variable = getClass().getSimpleName();

		@Override
		public String method() {
			return variable;
		}

		static String staticMethod() {
			return staticVariable;
		}

	}

	private static class Class3 extends Class2 {
		static String staticVariable = Class3.class.getSimpleName();
		String variable = getClass().getSimpleName();

		@Override
		public String method() {
			return variable;
		}

		static String staticMethod() {
			return staticVariable;
		}
	}

}

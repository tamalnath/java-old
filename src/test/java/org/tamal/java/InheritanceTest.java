package org.tamal.java;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Tamal Kanti Nath
 */
@SuppressWarnings({ "static-method", "static-access", "hiding", "synthetic-access" })
public class InheritanceTest {

	/**
	 * This test proves that a static methods, static class variables and class
	 * variables are resolved at compile time. But a non-static method is
	 * resolved at runtime. It also proves that static method can be called by
	 * null instance variables.
	 */
	@Test
	public void compileTimeVsRuntime() {
		Class1 x = null;
		Class2 y = null;
		Class3 z = null;

		assertEquals("Class1.staticMethod", x.staticMethod());
		assertEquals("Class2.staticMethod", y.staticMethod());
		assertEquals("Class3.staticMethod", z.staticMethod());

		assertEquals("Class1.staticVariable", x.staticVariable);
		assertEquals("Class2.staticVariable", y.staticVariable);
		assertEquals("Class3.staticVariable", z.staticVariable);

		x = new Class1();
		y = new Class2();
		z = new Class3();

		assertEquals("Class1.variable", x.variable);
		assertEquals("Class2.variable", y.variable);
		assertEquals("Class3.variable", z.variable);

		assertEquals("Class1.method", x.method());
		assertEquals("Class2.method", y.method());
		assertEquals("Class3.method", z.method());

		assertEquals("Class1.method", ((Interface1) x).method());
		assertEquals("Class2.method", ((Interface1) y).method());
		assertEquals("Class3.method", ((Interface1) z).method());

		assertEquals("Interface1.variable", ((Interface1) x).variable);
		assertEquals("Interface1.variable", ((Interface1) y).variable);
		assertEquals("Interface1.variable", ((Interface1) z).variable);

		x = new Class3();
		y = new Class3();
		z = new Class3();

		assertEquals("Class1.variable", x.variable);
		assertEquals("Class2.variable", y.variable);
		assertEquals("Class3.variable", z.variable);

		assertEquals("Class3.method", x.method());
		assertEquals("Class3.method", y.method());
		assertEquals("Class3.method", z.method());

		assertEquals("Class3.method", ((Interface2) x).method());
		assertEquals("Class3.method", ((Interface2) y).method());
		assertEquals("Class3.method", ((Interface2) z).method());

		assertEquals("Interface2.variable", ((Interface2) x).variable);
		assertEquals("Interface2.variable", ((Interface2) y).variable);
		assertEquals("Interface2.variable", ((Interface2) z).variable);

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
		static String staticVariable = "Class1.staticVariable";
		String variable = "Class1.variable";

		/**
		 * Although this method is declared as a default method in both the
		 * interfaces, we need to override this method to avoid ambiguity.
		 */
		@Override
		public String method() {
			return "Class1.method";
		}

		static String staticMethod() {
			return "Class1.staticMethod";
		}

	}

	private static class Class2 extends Class1 {
		static String staticVariable = "Class2.staticVariable";
		String variable = "Class2.variable";

		@Override
		public String method() {
			return "Class2.method";
		}

		static String staticMethod() {
			return "Class2.staticMethod";
		}

	}

	private static class Class3 extends Class2 {
		static String staticVariable = "Class3.staticVariable";
		String variable = "Class3.variable";

		@Override
		public String method() {
			return "Class3.method";
		}

		static String staticMethod() {
			return "Class3.staticMethod";
		}
	}

}

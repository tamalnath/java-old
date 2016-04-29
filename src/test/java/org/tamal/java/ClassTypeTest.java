package org.tamal.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * This test suite demonstrates the usage of static nested class, inner class, local class and anonymous class.
 * @author Tamal Kanti Nath
 */
@SuppressWarnings({ "static-method", "static-access", "hiding", "synthetic-access" })
public class ClassTypeTest {

	private String name = "InheritanceTest";

	/**
	 * This method demonstrates the use of static nested class. It also proves
	 * that non-static variables/methods of the enclosing class cannot be
	 * accessed from static nested class.
	 */
	@Test
	public void testStaticNestedClass() {
		ClassTypeTest.Static1.Static2.Static3.checkAccess();
	}

	private interface Interface {
		int value = 1;
	}

	private static class Static1 implements Interface {
		private static String name = "Static1";

		private interface Interface {
			int value = 2;
		}

		private static class Static2 implements Interface {

			private static String name = "Static2";

			private interface Interface {
				int value = 3;
			}

			private static class Static3 implements Interface {
				private static String name = "Static3";

				private static void checkAccess() {
					// non-static variable name cannot be referenced from a static context
					// assertEquals("InheritanceTest", InheritanceTest.name);

					assertEquals("Static3", Static3.name);
					assertEquals("Static2", Static2.name);
					assertEquals("Static1", Static1.name);

					assertEquals(3, Static3.value);
					assertEquals(2, Static2.value);
					assertEquals(1, Static1.value);
				}
			}
		}
	}

	/**
	 * This method demonstrates the use of inner class. This also proves that
	 * static variables/methods/classes (except static final variables) cannot
	 * be declared inside non-static inner class.
	 */
	@Test
	public void testInnerClass() {
		this.new Inner1().new Inner2().new Inner3().qualifiedThis();
	}

	class Inner1 {

		private String name = "Inner1";

		class Inner2 {
			private String name = "Inner2";

			class Inner3 {
				private String name = "Inner3";

				public void qualifiedThis() {
					String name = "local";
					assertEquals("local", name);
					assertEquals("Inner3", this.name);
					assertEquals("Inner2", Inner2.this.name);
					assertEquals("Inner1", Inner1.this.name);
					assertEquals("InheritanceTest", ClassTypeTest.this.name);
				}
			}
		}
	}

	/**
	 * This method demonstrates the use of local class.
	 */
	@Test
	public void testLocalClass() {
		class Local1 {
			String name = "Local1";

			public void method(String name1) {
				class Local2 {
					String name = "Local2";

					public void method(String name2) {
						class Local3 {
							String name = "Local3";

							public void method(String name3) {
								assertEquals("Local3", name3);
								assertEquals("Local2", name2);
								assertEquals("Local1", name1);

								assertEquals("Local3", Local3.this.name);
								assertEquals("Local2", Local2.this.name);
								assertEquals("Local1", Local1.this.name);
								assertEquals("InheritanceTest", ClassTypeTest.this.name);
							}
						}
						Local3 local = new Local3();
						local.method("Local3");
					}
				}
				Local2 local = new Local2();
				local.method("Local2");
			}
		}
		Local1 local = new Local1();
		local.method("Local1");
	}

	/**
	 * This method demonstrates the use of anonymous class.
	 */
	@Test
	public void testAnonymousClass() {
		int i1 = 1;
		Runnable r = new Runnable() {
			private int j1 = 1;

			@Override
			public void run() {
				int i2 = 2;
				Runnable r = new Runnable() {
					private int j2 = 2;

					@Override
					public void run() {
						int i3 = 3;
						Runnable r = new Runnable() {
							private int j3 = 3;

							@Override
							public void run() {
								assertEquals(1, i1);
								assertEquals(2, i2);
								assertEquals(3, i3);

								assertEquals(1, j1);
								assertEquals(2, j2);
								assertEquals(3, j3);
							}
						};
						r.run();
					}
				};
				r.run();
			}
		};
		r.run();
	}

}

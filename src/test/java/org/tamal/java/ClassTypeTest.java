package org.tamal.java;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * This test demonstrates the usage of static nested class, inner class, local class and anonymous class.
 *
 * @author Tamal Kanti Nath
 */
public class ClassTypeTest {

    private final String name = getClass().getSimpleName();

    /**
     * This method demonstrates the use of static nested class. It also proves
     * that non-static variables/methods of the enclosing class cannot be
     * accessed from static nested class.
     */
    @Test
    public void testStaticClass() {
        ClassTypeTest.Static1.Static2.Static3.checkAccess();
    }

    /**
     * This method demonstrates the use of local class.
     */
    @Test
    public void testLocalClass() {
        class Local1 {
            final String name = getClass().getSimpleName();

            public void method(String name1) {
                class Local2 {
                    final String name = getClass().getSimpleName();

                    public void method(String name2) {
                        class Local3 {
                            final String name = getClass().getSimpleName();

                            public void method(String name3) {
                                assertEquals(name3, "Local3");
                                assertEquals(name2, "Local2");
                                assertEquals(name1, "Local1");

                                assertEquals(Local3.this.name, "Local3");
                                assertEquals(Local2.this.name, "Local2");
                                assertEquals(Local1.this.name, "Local1");
                                assertEquals(ClassTypeTest.this.name, "ClassTypeTest");
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
     * This method demonstrates the use of inner class. This also proves that
     * static variables/methods/classes (except static final variables) cannot
     * be declared inside non-static inner class.
     */
    @Test
    public void testInnerClass() {
        this.new Inner1().new Inner2().new Inner3().qualifiedThis();
    }

    /**
     * This method demonstrates the use of anonymous class.
     */
    @Test
    public void testAnonymousClass() {
        Class<? extends ClassTypeTest> var1 = getClass();
        Runnable r = new Runnable() {
            private final Class<? extends Runnable> field1 = getClass();

            @Override
            public void run() {
                Class<? extends Runnable> var2 = getClass();
                Runnable r = new Runnable() {
                    private final Class<? extends Runnable> field2 = getClass();

                    @Override
                    public void run() {
                        Class<? extends Runnable> var3 = getClass();
                        Runnable r = new Runnable() {
                            private final Class<? extends Runnable> field3 = getClass();

                            @Override
                            public void run() {
                                assertEquals(field3.getEnclosingClass(), var3);
                                assertEquals(field2.getEnclosingClass(), var2);
                                assertEquals(field1.getEnclosingClass(), var1);
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

    private static class Static1 {
        private static final String name = Static1.class.getSimpleName();

        private static class Static2 {

            private static final String name = Static2.class.getSimpleName();

            private static class Static3 {

                private static final String name = Static3.class.getSimpleName();

                private static void checkAccess() {
                    assertEquals(name, "Static3");

                    assertEquals(Static3.name, "Static3");
                    assertEquals(Static2.name, "Static2");
                    assertEquals(Static1.name, "Static1");
                }
            }
        }
    }

    class Inner1 {

        private final String name = getClass().getSimpleName();

        class Inner2 {
            private final String name = getClass().getSimpleName();

            class Inner3 {
                private final String name = getClass().getSimpleName();

                public void qualifiedThis() {
                    assertEquals("Inner3", this.name);
                    assertEquals("Inner2", Inner2.this.name);
                    assertEquals("Inner1", Inner1.this.name);
                    assertEquals("ClassTypeTest", ClassTypeTest.this.name);
                }
            }
        }
    }

}

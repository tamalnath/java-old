package org.tamal.lombock;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.var;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class LombokTest {

    @Test
    void testLombok() {
        var employee1 = new Employee("Employee", 20);
        var employee2 = Employee.of("Employee", 20);
        assertEquals(employee1, employee2);
        assertEquals(employee1.hashCode(), employee2.hashCode());
        employee1.setName("Test");
        assertNotEquals(employee1, employee2);
        assertNotEquals(employee1.hashCode(), employee2.hashCode());

        var department = Department.builder().name("department").employee(employee1).employee(employee2).build();
        System.out.println(department);

    }

    /**
     * The employee bean.
     */
    @Data(staticConstructor = "of")
    static class Employee {

        @NonNull
        private String name;

        @NonNull
        private int age;
    }

    /**
     * The department bean.
     */
    @Builder
    @ToString
    static class Department {

        private String name;

        @Singular
        private List<Employee> employees;

    }

}

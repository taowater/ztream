package com.taowater.ztream;

import lombok.*;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;


@UtilityClass
public class TestClass {

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Student extends Person {
        private String name;
        private Integer age;
    }

    @Data
    public static class Person {
        private String sex;
        private Boolean flag;
        private boolean flag2;
        private int flag3;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Teacher extends Person {
        private String name;
        private Long money;
    }


    public static List<Student> testList = new ArrayList<>();

    static {
        testList.add(Student.builder().name("小猪").age(1).build());
        testList.add(Student.builder().name("小狗").age(2).build());
        testList.add(null);
        testList.add(Student.builder().name(null).age(3).build());
        testList.add(Student.builder().name(" ").age(9).build());
        testList.add(Student.builder().name("小狗").age(null).build());
        testList.add(Student.builder().name(null).age(4).build());
        testList.add(Student.builder().name("").age(89).build());
        testList.add(Student.builder().name("小猪").age(50).build());
        testList.add(Student.builder().name("小猪").age(7).build());
    }
}

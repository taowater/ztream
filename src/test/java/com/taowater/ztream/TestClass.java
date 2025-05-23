package com.taowater.ztream;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;


@UtilityClass
public class TestClass {

    @Data
    @Accessors(chain = true)
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
    }

    @Data
    public static class Teacher extends Person {
        private String name;
        private Long money;
    }


    public static List<Student> testList = new ArrayList<>();

    static {
        testList.add(new Student().setName("小猪").setAge(1));
        testList.add(new Student().setName("小狗").setAge(2));
        testList.add(null);
        testList.add(new Student().setName(null).setAge(3));
        testList.add(new Student().setName(" ").setAge(9));
        testList.add(new Student().setName("小狗").setAge(null));
        testList.add(new Student().setName(null).setAge(4));
        testList.add(new Student().setName("").setAge(89));
        testList.add(new Student().setName("小猪").setAge(50));
        testList.add(new Student().setName("小猪").setAge(7));
    }
}

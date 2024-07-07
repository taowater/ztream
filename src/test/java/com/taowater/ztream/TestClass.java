package com.taowater.ztream;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import org.dromara.hutool.core.collection.ListUtil;

import java.util.List;


@UtilityClass
public class TestClass {

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class Student extends Person {
        private String name;
        private Integer age;
    }

    public static class Person {
        private String sex;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Teacher extends Person {
        private String name;
        private Long money;
    }

    public static List<Student> testList = ListUtil.of(
            new Student().setName("小猪").setAge(123),
            new Student().setName("小狗").setAge(45),
            null,
            new Student().setName(null).setAge(564),
            new Student().setName("小狗").setAge(null),
            new Student().setName(null).setAge(70),
            new Student().setName("小猪").setAge(4),
            new Student().setName("小猪").setAge(null)
    );
}

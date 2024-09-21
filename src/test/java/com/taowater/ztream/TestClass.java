package com.taowater.ztream;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import org.dromara.hutool.core.collection.ListUtil;

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

    public static class Person {
        private String sex;
    }

    @Data
    public static class Teacher extends Person {
        private String name;
        private Long money;
    }

    public static List<Student> testList = ListUtil.of(
            new Student().setName("小猪").setAge(1),
            new Student().setName("小狗").setAge(2),
            null,
            new Student().setName(null).setAge(3),
            new Student().setName(" ").setAge(9),
            new Student().setName("小狗").setAge(null),
            new Student().setName(null).setAge(4),
            new Student().setName("").setAge(89),
            new Student().setName("小猪").setAge(50),
            new Student().setName("小猪").setAge(7)
    );
}

package com.zistory.ztream;

import io.github.taowater.ztream.Ztream;
import io.github.taowater.ztream.MyCollectors;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.var;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.collection.ListUtil;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ZtreamTest {

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

    @Test
    @SneakyThrows
    public void test() {

        List<Student> list = ListUtil.of(
                new Student().setName("小猪").setAge(23),
                new Student().setName("小狗").setAge(24),
                new Student().setName("小猪").setAge(25),
                new Student().setName("小猪").setAge(26)
        );
        String join = Ztream.of(list).join(Student::getName);

        System.out.println(join);


        var group = Ztream.of(list).distinct(Student::getName).toMap(Student::getAge, Student::getName, LinkedHashMap::new);

        group.forEach((key, value) -> System.out.println(key + ":" + value));

        List<Student> students = Ztream.of(list).distinct().toList();
        students.forEach(System.out::println);

        var group1 = Ztream.of(students).groupBy(Student::getName, Student::getAge, HashMap::new, Collectors.toSet());
        var group3 = students.stream().collect(Collectors.groupingBy(Student::getName));
        var group4 = Ztream.of(students).groupBy(Student::getName, Student::getAge, LinkedHashMap::new, Collectors.toSet());
        var map = Ztream.of(students).toMap(Object::toString, Objects::toString);

        var sum = Ztream.of(students).sum(Student::getAge);
        var max = Ztream.of(students).max(Student::getAge);
        var min = Ztream.of(students).min(Student::getAge);

        System.out.println(sum);
        System.out.println(max);
        System.out.println(min);
        Ztream.of(students).forEach((e, i) -> {
            System.out.println("第" + (i + 1) + "位学生：" + e.getName());
        });
    }

    @Test
    @SneakyThrows
    public void test2() {

        List<String> list = null;
        List<String> list2 = ListUtil.of(null, "123");

        Ztream.of(list).forEach(e -> System.out.println(e.toString()));
        Ztream.of(list2).filter(Objects::nonNull).forEach(System.out::println);

    }

    @Test
    public void distinct() {
        List<Integer> list = ListUtil.of(1, 2, 3, 4, 5, 6, 7, 8);
        Ztream.of(list).distinct(i -> i % 3, true).forEach(System.out::println);
        System.out.println("==========");
        Ztream.of(list).distinct(i -> i % 3, false).forEach(System.out::println);

        CollUtil.distinct(list, i -> i % 3, true).forEach(System.out::println);
        System.out.println("==========");
        CollUtil.distinct(list, i -> i % 3, false).forEach(System.out::println);
    }

    @Test
    public void testToMap() {
        List<Student> list = ListUtil.of(
                new Student().setName(null).setAge(23),
                new Student().setName("小狗").setAge(24),
                new Student().setName("小猪").setAge(54),
                new Student().setName(null).setAge(null),
                new Student().setName("小猪").setAge(56)
        );

        Map<String, Integer> map = Ztream.of(list).toMap(Student::getName, Student::getAge);
        map.forEach((k, v) -> {
            System.out.println(MessageFormat.format("{0}:{1}", k, v));
        });

        Map<String, String> group = Ztream.of(list).groupBy(Student::getName, MyCollectors.join(","));
        group.forEach((k, v) -> {
            System.out.println(MessageFormat.format("{0}:{1}", k, v));
        });

        Ztream.of(list).distinct(Student::getName).forEach(System.out::println);
        Ztream.of(list).distinct(Student::getName, false).forEach(System.out::println);
    }

    @Test
    public void testMapIdx() {

        List<Student> list = ListUtil.of(
                new Student().setName(null).setAge(23),
                new Student().setName("小狗").setAge(24),
                new Student().setName("小猪").setAge(54),
                new Student().setName(null).setAge(null),
                new Student().setName("小猪").setAge(56)
        );

        List<Integer> list1 = Ztream.of(list).nonNull(Student::getAge).map((e, i) -> e.getAge() + i).toList();

        list1.forEach(System.out::println);
    }

    @Test
    public void ttt() {
        List<Student> list = ListUtil.of(
                new Student().setName("小猪").setAge(23),
                new Student().setName("小狗").setAge(24),
                new Student().setName("小狗").setAge(23),
                new Student().setName("小狗").setAge(23),
                new Student().setName("小猪").setAge(23),
                new Student().setName("小猪").setAge(25),
                new Student().setName("小猪").setAge(26)
        );


        var stringMapMap = Ztream.of(list).groupBilayer(Student::getAge, Student::getName);
        System.out.println(123);
    }

    @Test
    public void testFlat() {
        List<Integer> list = ListUtil.of(1, 2, 4);
        Function<Integer, Collection<Integer>> fun = e->   ListUtil.of(1,2,3);
        List<Integer> list1 = Ztream.of(list).flat(fun).toList();
        System.out.println(123);
    }

}
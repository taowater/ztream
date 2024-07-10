package com.taowater.ztream;

import com.taowater.ztream.TestClass.Student;
import lombok.SneakyThrows;
import lombok.var;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.collection.set.SetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.taowater.ztream.TestClass.testList;

class ZtreamTest {

    @Test
    void group() {
        var group = Ztream.of(testList).groupBy(Student::getName, Student::getAge, HashMap::new, Collectors.toSet());

        Map<String, Set<Integer>> group2 = new HashMap<>();
        for (var item : testList) {
            if (item == null) {
                group2.computeIfAbsent(null, k -> new HashSet<>()).add(null);
                continue;
            }
            group2.computeIfAbsent(item.getName(), k -> new HashSet<>()).add(item.getAge());
        }
        equals(
                group,
                group2
        );

        equals(
                Ztream.of(testList).group(Student::getName, Student::getAge, Collectors.toSet()),
                group2
        );
    }

    @Test
    void join() {

        equals(
                Ztream.of(testList).join(Student::getName),
                testList.stream().map(e -> {
                    if (Objects.isNull(e)) {
                        return null;
                    }
                    return e.getName();
                }).collect(Collectors.joining(","))
        );
    }

    @Test
    @SneakyThrows
    void nonNull() {

        equals(
                Ztream.of(testList).nonNull(),
                testList.stream().filter(Objects::nonNull)
        );

        equals(
                Ztream.of(testList).nonNull(Student::getName),
                testList.stream().filter(e -> Objects.nonNull(e) && Objects.nonNull(e.getName()))
        );
    }

    @Test
    void distinct() {
        List<Integer> list = ListUtil.of(1, 4, 5, 9, 3, 2, 3, 4, 5, 6, 7, 8);

        equals(
                Ztream.of(list).distinct(),
                list.stream().distinct()
        );

        equals(
                Ztream.of(list).distinct(i -> i % 3),
                CollUtil.distinct(list, i -> i % 3, false)
        );
    }

    @Test
    void hash() {

        Map<String, Integer> map = new HashMap<>();
        for (var item : testList) {
            if (item == null) {
                map.put(null, null);
                continue;
            }
            map.put(item.getName(), item.getAge());
        }

        equals(
                Ztream.of(testList).toMap(Student::getName, Student::getAge),
                map
        );

        equals(
                Ztream.of(testList).hash(Student::getName, Student::getAge),
                map
        );
    }

    @Test
    void testFlat() {
        List<Integer> list = ListUtil.of(1, 2, 4, 6, 4, 8, 9);
        Function<Integer, Collection<Integer>> fun = e -> ListUtil.of(1, 2, 3);
        equals(
                Ztream.of(list).flat(fun),
                list.stream().map(fun).flatMap(Collection::stream)
        );
    }

    @Test
    void sort() {
        equals(
                Ztream.of(testList).sort(r -> r
                        .desc(Student::getName, false)
                        .asc(Student::getAge)
                        .nullLast()
                ),
                testList.stream().sorted(
                        Comparator.nullsLast(
                                Comparator.comparing(Student::getName, Comparator.nullsLast(Comparator.reverseOrder()))
                                        .thenComparing(Student::getAge, Comparator.nullsFirst(Comparator.naturalOrder()))
                        )
                )
        );


        equals(
                Ztream.of(testList).sort(r -> r
                        .desc(e -> Objects.nonNull(e.getName()))
                        .nullLast()
                ),
                testList.stream().sorted(
                        Comparator.nullsLast(Comparator.comparing(e -> Objects.nonNull(e.getName()), Comparator.nullsFirst(Comparator.reverseOrder())))
                )
        );

        equals(
                Ztream.of(testList).sort(r -> r
                        .desc(Student::getName)
                        .desc(Student::getAge)
                ),
                testList.stream().sorted(
                        Comparator.nullsFirst(
                                Comparator.comparing(Student::getName, Comparator.nullsFirst(Comparator.reverseOrder()))
                                        .thenComparing(Student::getAge, Comparator.nullsFirst(Comparator.reverseOrder()))
                        )
                ));
    }

    @Test
    void avg() {

        List<Integer> list = ListUtil.of(12, 6, 8, 23, 5, 0);

        equals(
                Ztream.of(list).avg(e -> e, 0).doubleValue(),
                list.stream().mapToInt(e -> e).average().orElse(0.0)
        );
    }

    @Test
    void testFilter() {

        List<Student> list = ListUtil.of(
                new Student().setName("小猪").setAge(123),
                new Student().setName("小狗").setAge(45),
                new Student().setName(null).setAge(564),
                new Student().setName("小狗").setAge(null),
                new Student().setName(null).setAge(70),
                new Student().setName("小猪").setAge(4),
                new Student().setName("小猪").setAge(null)
        );
        System.out.println("------");
        Ztream.of(list).lt(Student::getAge, 4).forEach(System.out::println);
        System.out.println("------");
        Ztream.of(list).le(Student::getAge, 4).forEach(System.out::println);
        System.out.println("------");
        Ztream.of(list).gt(Student::getAge, 70).forEach(System.out::println);
        System.out.println("------");
        Ztream.of(list).ge(Student::getAge, 70).forEach(System.out::println);
        System.out.println("------");
        Ztream.of(list).between(Student::getAge, 69, 71).forEach(System.out::println);
    }

    @Test
    void append() {
        List<Integer> list = ListUtil.of(1, 2, 3, 4, 5, 6);
        equals(
                Ztream.of(list).distinct().append(Ztream.of(7, 8, 9)),
                ListUtil.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );
        equals(
                Ztream.of(list).distinct().append(7, 8, 9),
                ListUtil.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );
        equals(
                Ztream.of(list).append(SetUtil.of(7, 8, 8, 9)),
                ListUtil.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );

        equals(
                Ztream.of(list).append(Ztream.of(7, 8, 9)),
                ListUtil.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );
    }


    @Test
    void testShuffle() {
        Ztream.of(testList).shuffle().limit(3).nonNull().map(Student::getAge).forEach(e -> System.out.println(e));
    }

    public static void equals(Object o1, Object o2) {
//        System.out.println("----equals----");
//        System.out.println(o1);
//        System.out.println(o2);
        Assertions.assertEquals(o1, o2);
    }

    public static void equals(Ztream<?> ztream, Stream<?> stream) {
        Assertions.assertEquals(ztream.toList(), stream.collect(Collectors.toList()));
    }

    public static void equals(EntryZtream<?, ?> ztream, Map<?, ?> map) {
        equals(ztream.toMap(), map);
    }

    public static void equals(Ztream<?> ztream, Collection<?> coll) {
        equals(ztream.toList(), coll);
    }

}
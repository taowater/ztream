package com.taowater.ztream;

import com.taowater.taol.core.util.EmptyUtil;
import com.taowater.ztream.TestClass.Student;
import com.taowater.ztream.assist.Functions;
import lombok.SneakyThrows;
import lombok.var;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.text.SimpleDateFormat;
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
        List<Integer> list = newList(1, 4, 5, 9, 3, 2, 3, 4, 5, 6, 7, 8);

        equals(
                Ztream.of(list).distinct(),
                list.stream().distinct()
        );

        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (Integer i : list) {
            map.putIfAbsent(i % 3, i);
        }

        equals(
                Ztream.of(list).distinct(i -> i % 3, false),
                map.values().stream()
        );
    }

    @Test
    void hash() {

        Map<String, Integer> map = new HashMap<>();
        Map<String, Student> oMap = new HashMap<>();
        for (var item : testList) {
            if (item == null) {
                map.put(null, null);
                oMap.put(null, null);
                continue;
            }
            map.put(item.getName(), item.getAge());
            oMap.put(item.getName(), item);
        }

        equals(
                Ztream.of(testList).toMap(Student::getName, Student::getAge),
                map
        );

        equals(
                Ztream.of(testList).toMap(Student::getName),
                oMap
        );

        equals(
                Ztream.of(testList).hash(Student::getName, Student::getAge),
                map
        );

        assert Ztream.of(testList).toMap(Student::getName, () -> new LinkedHashMap<>()) instanceof LinkedHashMap;


    }

    @Test
    void flat() {
        List<Integer> list = newList(1, 2, 4, 6, 4, 8, 9);
        Function<Integer, Collection<Integer>> fun = e -> newList(1, 2, 3);
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
                        .asc(false, Student::getAge)
                        .nullFirst(false)
                ),
                testList.stream().sorted(
                        Comparator.nullsLast(
                                Comparator.comparing(Student::getName, Comparator.nullsLast(Comparator.reverseOrder()))
                        )
                )
        );

        equals(
                Ztream.of(testList).sort(r -> r
                        .desc(Student::getName, false)
                        .asc(Student::getAge)
                        .nullFirst(false)
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
                        .nullFirst(false)
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
    void filter() {

        equals(
                Ztream.of(testList).lt(Student::getAge, 5),
                testList.stream().filter(e -> Objects.nonNull(e) && Objects.nonNull(e.getAge()) && e.getAge() < 5)
        );

        equals(
                Ztream.of(testList).le(Student::getAge, 5),
                testList.stream().filter(e -> Objects.nonNull(e) && Objects.nonNull(e.getAge()) && e.getAge() <= 5)
        );

        equals(
                Ztream.of(testList).gt(Student::getAge, 7),
                testList.stream().filter(e -> Objects.nonNull(e) && Objects.nonNull(e.getAge()) && e.getAge() > 7)
        );

        equals(
                Ztream.of(testList).between(Student::getAge, 3, 8),
                testList.stream().filter(e -> Objects.nonNull(e) && Objects.nonNull(e.getAge()) && e.getAge() >= 3 && e.getAge() <= 8)
        );

        equals(
                Ztream.of(testList).ge(Student::getAge, 3),
                testList.stream().filter(e -> Objects.nonNull(e) && Objects.nonNull(e.getAge()) && e.getAge() >= 3)
        );

        equals(
                Ztream.of(testList).eq(Student::getName, "小猪"),
                testList.stream().filter(e -> Objects.equals("小猪", Any.of(e).get(Student::getName)))
        );

        equals(
                Ztream.of(testList).eq(Student::getName, null),
                testList.stream().filter(e -> Objects.equals(null, Any.of(e).get(Student::getName)))
        );

        equals(
                Ztream.of(testList).isNull(Student::getName),
                testList.stream().filter(e -> Objects.equals(null, Any.of(e).get(Student::getName)))
        );

        equals(
                Ztream.of(testList).in(Student::getName, "小猪", null),
                testList.stream().filter(e -> newList("小猪", null).contains(Any.of(e).get(Student::getName)))
        );

        equals(
                Ztream.of(testList).notIn(Student::getName, (String) null, "小猪"),
                testList.stream().filter(e -> !newList((String) null, "小猪").contains(Any.of(e).get(Student::getName)))
        );

        equals(
                Ztream.of(testList).isNull(),
                testList.stream().filter(Objects::isNull)
        );

        equals(
                Ztream.of(testList).isBlank(Student::getName),
                testList.stream().filter(e -> StringUtils.isBlank(Any.of(e).get(Student::getName)))
        );

        equals(
                Ztream.of(testList).nonBlank(Student::getName),
                testList.stream().filter(e -> StringUtils.isNotBlank((Any.of(e).get(Student::getName))))
        );

        equals(
                Ztream.of(testList).nonEmpty(Student::getName),
                testList.stream().filter(e -> EmptyUtil.isNotEmpty((Any.of(e).get(Student::getName))))
        );
        equals(
                Ztream.of(testList).rightLike(Student::getName, "小"),
                testList.stream().filter(e -> {
                    String str = Any.of(e).get(Student::getName);
                    if (EmptyUtil.isEmpty(str)) {
                        return false;
                    }
                    return str.startsWith("小");
                })
        );

        Ztream.of(testList).query(w -> w
                .eq(Student::getAge, 2)
                .in(Student::getName, "2134")
                .or(w2 -> w2
                        .in(Student::getName, "123")
                )
        ).toList();

        equals(
                Ztream.of(testList).filter(Objects::isNull),
                testList.stream().filter(Objects::isNull)
        );

        equals(
                Ztream.of(testList).nonNull().isTrue(TestClass.Person::getFlag),
                testList.stream().filter(e -> {
                    if (e == null) {
                        return false;
                    }
                    Boolean flag = Any.of(e).get(TestClass.Person::getFlag);
                    if (flag == null) {
                        return false;
                    }
                    return flag;
                })
        );
    }

    @Test
    void wrapper() {
        Ztream.of(testList).query(w -> w
                .gt(Student::getAge, 20)
        ).forEach(System.out::println);
    }

    @Test
    void peak() {

        var o = Ztream.of(testList).maxBy(Student::getAge).orElse(null);
        var o2 = Ztream.of(testList).minBy(Student::getAge, false).orElse(null);
        System.out.println(o);
        System.out.println(o2);

        var list = newList(null, 1, 2, 3, 4, 5, 6, null);

        equals(
                Ztream.of(list).min(false).orElse(null),
                1
        );

        equals(
                Ztream.of(list).max().orElse(null),
                6
        );

        var today = new Date();
        var yesterday = new Date(today.getTime() - 1000 * 60 * 24L);
        var tomorrow = new Date(today.getTime() + 1000 * 60 * 24L);

        var dates = newList(yesterday, today, tomorrow);

        equals(
                Ztream.of(dates).max().get(ZtreamTest::formatDate),
                ZtreamTest.formatDate(tomorrow)
        );

        equals(
                Ztream.of(dates).min().get(ZtreamTest::formatDate),
                ZtreamTest.formatDate(yesterday)
        );
    }

    @Test
    void math() {

        equals(
                Ztream.of(testList).avg(Student::getAge, 0),
                (int) (testList.stream().mapToInt(e -> Any.of(e).map(Student::getAge).orElse(0)).average().getAsDouble())
        );

        equals(
                Ztream.of(testList).sum(Student::getAge),
                testList.stream().mapToInt(e -> Any.of(e).map(Student::getAge).orElse(0)).sum()
        );

        equals(
                Ztream.of(testList).max(Student::getAge),
                testList.stream().mapToInt(e -> Any.of(e).map(Student::getAge).orElse(Integer.MIN_VALUE)).max().getAsInt()
        );

        equals(
                Ztream.of(testList).nonNull(Student::getAge).min(Student::getAge),
                testList.stream().mapToInt(e -> Any.of(e).map(Student::getAge).orElse(Integer.MAX_VALUE)).min().getAsInt()
        );
    }

    @Test
    void collect() {
        equals(
                Ztream.of(testList).toList(),
                testList
        );

        equals(
                Ztream.of(testList).toList(Student::getName),
                testList.stream().map(e -> Any.of(e).get(Student::getName)).collect(Collectors.toList())
        );
        equals(
                Ztream.of(testList).toList(TestClass.Teacher.class),
                testList.stream().map(e -> Any.of(e).get(TestClass.Teacher.class)).collect(Collectors.toList())
        );

        equals(
                Ztream.of(testList).toSet(),
                new HashSet<>(testList)
        );

        equals(
                Ztream.of(testList).toSet(Student::getName),
                testList.stream().map(e -> Any.of(e).get(Student::getName)).collect(Collectors.toSet())
        );
        equals(
                Ztream.of(testList).toSet(TestClass.Teacher.class),
                testList.stream().map(e -> Any.of(e).get(TestClass.Teacher.class)).collect(Collectors.toSet())
        );


    }

    @Test
    void append() {
        List<Integer> list = newList(1, 2, 3, 4, 5, 6);
        equals(
                Ztream.of(list).distinct().append(Ztream.of(7, 8, 9)),
                newList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );
        equals(
                Ztream.of(list).distinct().append(7, 8, 9),
                newList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );
        equals(
                Ztream.of(list).append(newSet(7, 8, 8, 9)),
                newList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );

        equals(
                Ztream.of(list).append(Ztream.of(7, 8, 9)),
                newList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );
    }


    @Test
    void judge() {
        Assertions.assertFalse(Ztream.of(testList).hadRepeat());
        Assertions.assertTrue(Ztream.of(testList).hadRepeat(Student::getName));
        Assertions.assertTrue(Ztream.of(testList).anyMatch(Student::getName, Objects::isNull));
        Assertions.assertFalse(Ztream.of(testList).noneMatch(Student::getName, Objects::isNull));
        Assertions.assertFalse(Ztream.of(testList).allMatch(Student::getName, Objects::isNull));


        Assertions.assertFalse(Ztream.of(null, null).isEmpty());
        Assertions.assertTrue(Ztream.of(null, null).nonNull().isEmpty());
        Assertions.assertTrue(Ztream.of(null, null).isNotEmpty());

        Assertions.assertFalse(Ztream.of((Boolean) null, null).allMatch(Functions.of(e -> e)));

    }

    @Test
    void shuffle() {
        Ztream.of(testList).shuffle().limit(3).nonNull().map(Student::getAge).forEach(System.out::println);
    }

    @Test
    void query() {
        equals(
                Ztream.of(testList).last().orElse(null),
                testList.get(testList.size() - 1)
        );
    }

    @Test
    void page() {
        equals(
                Ztream.of(testList).page(2, 3).toList(),
                testList.subList(3, 6)
        );
    }

    public static <T> void equals(T o1, T o2) {
        Assertions.assertEquals(o1, o2);
    }

    public static void equals(Ztream<?> ztream, Stream<?> stream) {
        equals(ztream.toList(), stream.collect(Collectors.toList()));
    }

    public static void equals(EntryZtream<?, ?> ztream, Map<?, ?> map) {
        equals(ztream.toMap(), map);
    }

    public static void equals(Ztream<?> ztream, Collection<?> coll) {
        equals(ztream.toList(), coll);
    }

    public static <T> List<T> newList(T... values) {
        List<T> list = new ArrayList<>();
        list.addAll(Arrays.asList(values));
        return list;
    }

    public static <T> Set<T> newSet(T... values) {
        Set<T> set = new HashSet<>();
        set.addAll(Arrays.asList(values));
        return set;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
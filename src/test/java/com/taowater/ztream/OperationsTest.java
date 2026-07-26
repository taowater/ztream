package com.taowater.ztream;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class OperationsTest {

    @Test
    void collectOverloadsSupportFactoriesMappersAndConversions() {
        TestFixtures.Student student = TestFixtures.student("Ada", 12);

        assertEquals(Arrays.asList("1", "2"),
                Ztream.of(1, 2).collect(String::valueOf, LinkedList::new));
        assertEquals(Arrays.asList(1, 2), Ztream.of(1, 2)
                .collect((Supplier<ArrayList<Integer>>) ArrayList::new));
        assertEquals("Ada", Ztream.of(student)
                .collect(TestFixtures.Teacher.class, LinkedList::new).get(0).getName());
        assertEquals(Arrays.asList("1", "2"), Ztream.of(1, 2).collect(String::valueOf));
        assertEquals("Ada", Ztream.of(student).collect(TestFixtures.Teacher.class).get(0).getName());
        assertEquals(Arrays.asList(1, 2), Ztream.of(1, 2).toList());
        assertEquals(Arrays.asList("1", "2"), Ztream.of(1, 2).toList(String::valueOf));
        assertEquals("Ada", Ztream.of(student).toList(TestFixtures.Teacher.class).get(0).getName());
        assertEquals(new HashSet<>(Arrays.asList(1, 2)), Ztream.of(1, 2, 2).toSet());
        assertEquals(new HashSet<>(Arrays.asList("1", "2")),
                Ztream.of(1, 2, 2).toSet(String::valueOf));
        assertEquals(1, Ztream.of(student).toSet(TestFixtures.Teacher.class).size());
    }

    @Test
    void joinOverloadsHandleMappingNullsAndDecoration() {
        assertEquals("a,b", Ztream.of("a", "b").join());
        assertEquals("a|b", Ztream.of("a", "b").join("|"));
        assertEquals("1,2", Ztream.of("a", "bb").join(String::length));
        assertEquals("1|2", Ztream.of("a", "bb").join(String::length, "|"));
        assertEquals("[a,null,b]", Ztream.of("a", null, "b").join(",", "[", "]"));
        assertEquals("a,b", Ztream.of("a", "b").join(null, "", ""));
    }

    @Test
    void groupByOverloadsSupportNullKeysMapFactoriesAndDownstreams() {
        List<String> values = Arrays.asList("a", "bb", "c", null);
        Map<Integer, List<String>> expected = new HashMap<>();
        expected.put(1, Arrays.asList("a", "c"));
        expected.put(2, Arrays.asList("bb"));
        expected.put(null, Arrays.asList((String) null));

        assertEquals(expected, Ztream.of(values).groupBy(String::length));
        assertTrue(Ztream.of(values).groupBy(String::length,
                (Supplier<LinkedHashMap<Integer, List<String>>>) LinkedHashMap::new) instanceof LinkedHashMap);
        assertEquals(new HashSet<>(Arrays.asList("a", "c")),
                Ztream.of(values).groupBy(String::length, Collectors.toSet()).get(1));
        assertTrue(Ztream.of("a", "bb").groupBy(String::length,
                (Supplier<TreeMap<Integer, List<String>>>) TreeMap::new,
                Collectors.toList()) instanceof TreeMap);
        assertEquals(Arrays.asList(1, 1), Ztream.of("a", "c").groupBy(String::length, String::length).get(1));
        assertTrue(Ztream.of("a", "bb").groupBy(String::length, String::toUpperCase,
                LinkedHashMap::new) instanceof LinkedHashMap);
        assertEquals(new LinkedHashSet<>(Arrays.asList("A", "C")),
                Ztream.of("a", "c").groupBy(String::length, String::toUpperCase,
                        Collectors.toCollection(LinkedHashSet::new)).get(1));
        assertTrue(Ztream.of("a", "bb").groupBy(String::length, String::toUpperCase,
                LinkedHashMap::new, Collectors.toList()) instanceof LinkedHashMap);
    }

    @Test
    void groupByCollectorCombinesParallelPartitions() {
        Map<Integer, Long> grouped = Ztream.range(0, 100).parallel()
                .groupBy(v -> v % 2, Collectors.counting());

        assertEquals(Long.valueOf(50), grouped.get(0));
        assertEquals(Long.valueOf(50), grouped.get(1));
    }

    @Test
    void bilayerGroupingSupportsWholeValuesAndMappedValues() {
        Map<Character, Map<Integer, List<String>>> grouped = Ztream.of("a", "ab", "b")
                .groupBilayer(v -> v.charAt(0), String::length);
        Map<Character, Map<Integer, List<Integer>>> lengths = Ztream.of("a", "ab", "b")
                .groupBilayer(v -> v.charAt(0), String::length, String::length);

        assertEquals(Arrays.asList("a"), grouped.get('a').get(1));
        assertEquals(Arrays.asList("ab"), grouped.get('a').get(2));
        assertEquals(Arrays.asList(1), lengths.get('b').get(1));
    }

    @Test
    void toMapOverloadsKeepFirstDuplicateAndSupportNullElements() {
        List<String> values = Arrays.asList("a", "bb", "c", null);

        assertEquals("a", Ztream.of(values).toMap(String::length).get(1));
        assertNull(Ztream.of(values).toMap(String::length).get(null));
        assertTrue(Ztream.of(values).toMap(String::length,
                (Supplier<LinkedHashMap<Integer, String>>) LinkedHashMap::new) instanceof LinkedHashMap);
        assertEquals(Integer.valueOf(1), Ztream.of(values).toMap(String::length, String::length).get(1));
        assertTrue(Ztream.of(values).toMap(String::length, String::length,
                LinkedHashMap::new) instanceof LinkedHashMap);
    }

    @Test
    void hashAndGroupEntryOperationsCoverEveryOverload() {
        assertEquals("a", Ztream.of("a", "bb", "c").hash(String::length).toMap().get(1));
        assertEquals(Integer.valueOf(1),
                Ztream.of("a", "bb", "c").hash(String::length, String::length).toMap().get(1));
        assertEquals(Arrays.asList("a", "c"),
                Ztream.of("a", "bb", "c").group(String::length).toMap().get(1));
        assertEquals(Arrays.asList("A", "C"), Ztream.of("a", "bb", "c")
                .group(String::length, String::toUpperCase).toMap().get(1));
        assertEquals(new HashSet<>(Arrays.asList("A", "C")), Ztream.of("a", "bb", "c")
                .group(String::length, String::toUpperCase, Collectors.toSet()).toMap().get(1));
    }

    @Test
    void distinctConditionAndJudgeMethodsCoverTrueAndFalseBranches() {
        assertEquals(Arrays.asList("a", "bb"),
                Ztream.of("a", "c", "bb").distinct(true, String::length).toList());
        assertEquals(Arrays.asList("a", "c", "bb"),
                Ztream.of("a", "c", "bb").distinct(false, String::length).toList());
        assertEquals(Arrays.asList("a", "a"), Ztream.of("a", "a").distinct(false).toList());
        assertTrue(Ztream.of("a", "a").hadRepeat());
        assertTrue(Ztream.of("a", "b").hadRepeat(String::length));
        assertTrue(Ztream.of("a", "bb").anyMatch(String::length, v -> v == 2));
        assertTrue(Ztream.of("a", "bb").allMatch(String::length, v -> v > 0));
        assertTrue(Ztream.of("a", "bb").noneMatch(String::length, v -> v < 0));
        assertTrue(Ztream.empty().isEmpty());
        assertTrue(Ztream.of(1).isNotEmpty());
    }
}

package com.taowater.ztream;

import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EntryZtreamTest {

    private static List<Map.Entry<String, Integer>> entries() {
        return Arrays.asList(new SimpleEntry<>("a", 1), new SimpleEntry<>("b", 2));
    }

    @Test
    void factoriesMappingKeysValuesAndMapCollectionWork() {
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("a", 1);
        expected.put("b", 2);

        assertTrue(EntryZtream.empty().toMap().isEmpty());
        assertEquals(Arrays.asList("a", "b"), EntryZtream.of(entries()).keys().toList());
        assertEquals(Arrays.asList(1, 2), EntryZtream.of(entries()).values().toList());
        assertEquals(Arrays.asList("a1", "b2"),
                EntryZtream.of(entries()).map(e -> e.getKey() + e.getValue()).toList());
        assertEquals(Arrays.asList("a", "1", "b", "2"), EntryZtream.of(entries())
                .flatMap(e -> Stream.of(e.getKey(), String.valueOf(e.getValue()))).toList());
        assertEquals(Arrays.asList("a1", "b2"),
                EntryZtream.of(entries()).map((key, value) -> key + value).toList());
        assertEquals(expected, EntryZtream.of(entries()).toMap(LinkedHashMap::new));
        assertEquals(2, EntryZtream.of(entries().stream()).count());
    }

    @Test
    void forEachAndPeekVariantsExposeKeysAndValues() {
        List<String> seen = new ArrayList<>();

        EntryZtream.of(entries()).forEachKeyValue((k, v) -> seen.add(k + v));
        EntryZtream.of(entries()).forEachKey(seen::add);
        EntryZtream.of(entries()).forEachValue(v -> seen.add(String.valueOf(v)));
        EntryZtream.of(entries()).peekKeyValue((k, v) -> seen.add("p" + k + v)).toList();
        EntryZtream.of(entries()).peekKey(k -> seen.add("k" + k)).toList();
        EntryZtream.of(entries()).peekValue(v -> seen.add("v" + v)).toList();

        assertEquals(Arrays.asList(
                "a1", "b2", "a", "b", "1", "2",
                "pa1", "pb2", "ka", "kb", "v1", "v2"), seen);
    }

    @Test
    void filtersAndBiPredicateMatchesCoverBothOutcomes() {
        List<Map.Entry<String, Integer>> withNulls = Arrays.asList(
                new SimpleEntry<>(null, 0), new SimpleEntry<>("a", null), new SimpleEntry<>("b", 2));

        assertEquals(2, EntryZtream.of(withNulls).nonNullKey().count());
        assertEquals(2, EntryZtream.of(withNulls).nonNullValue().count());
        assertEquals(1, EntryZtream.of(withNulls).filter((k, v) -> k != null && v != null).count());
        assertEquals(1, EntryZtream.of(withNulls).filterKey("a"::equals).count());
        assertEquals(1, EntryZtream.of(withNulls).filterValue(Integer.valueOf(2)::equals).count());
        assertTrue(EntryZtream.of(entries()).anyMatch((k, v) -> v == 2));
        assertTrue(EntryZtream.of(entries()).allMatch((k, v) -> k != null));
        assertTrue(EntryZtream.of(entries()).noneMatch((k, v) -> v < 0));
    }

    @Test
    void keyValueTransformsFlipAndDistinctValueAreComposable() {
        List<Map.Entry<String, Integer>> duplicates = Arrays.asList(
                new SimpleEntry<>("a", 1), new SimpleEntry<>("b", 1), new SimpleEntry<>("c", 2));

        assertEquals(Arrays.asList("A", "B", "C"),
                EntryZtream.of(duplicates).mapKey(String::toUpperCase).keys().toList());
        assertEquals(Arrays.asList(10, 10, 20),
                EntryZtream.of(duplicates).mapValue(v -> v * 10).values().toList());
        assertEquals(Arrays.asList("a1", "b1", "c2"),
                EntryZtream.of(duplicates).mapKeyValue((k, v) -> k + v).toList());
        assertEquals(Arrays.asList(1, 1, 2), EntryZtream.of(duplicates).flip().keys().toList());
        assertEquals(Arrays.asList("a", "c"),
                EntryZtream.of(duplicates).distinctValue().keys().toList());
    }

    @Test
    void ztreamProducesIndependentEnhancedEntryStream() {
        AtomicInteger consumed = new AtomicInteger();
        EntryZtream<String, Integer> source = EntryZtream.of(entries());

        Map<String, Integer> result = source.ztream(source.stream().peek(e -> consumed.incrementAndGet())).toMap();

        assertEquals(2, consumed.get());
        assertEquals(Integer.valueOf(1), result.get("a"));
    }

    @Test
    void toMapUsesIndependentContainersWhenStreamIsParallel() {
        Map<String, Integer> result = EntryZtream.of(Stream.iterate(0, value -> value + 1)
                        .limit(10_000)
                        .parallel()
                        .map(value -> new SimpleEntry<>(String.valueOf(value), value)))
                .toMap();

        assertEquals(10_000, result.size());
        assertEquals(Integer.valueOf(0), result.get("0"));
        assertEquals(Integer.valueOf(9_999), result.get("9999"));
    }

    @Test
    void parallelToMapKeepsEncounterOrderForDuplicateKeys() {
        AtomicInteger containerCount = new AtomicInteger();
        Map<Integer, Integer> result = EntryZtream.of(Stream.iterate(0, value -> value + 1)
                        .limit(10_000)
                        .parallel()
                        .map(value -> new SimpleEntry<>(value % 10, value)))
                .toMap(() -> {
                    containerCount.incrementAndGet();
                    return new LinkedHashMap<>();
                });

        assertTrue(containerCount.get() > 1);
        assertEquals(10, result.size());
        for (int key = 0; key < 10; key++) {
            assertEquals(Integer.valueOf(9_990 + key), result.get(key));
        }
    }

    @Test
    void parallelToMapRetainsNullKeyAndValueSupport() {
        List<Map.Entry<String, Integer>> withNulls = Arrays.asList(
                new SimpleEntry<>(null, 1),
                new SimpleEntry<>("a", null),
                new SimpleEntry<>(null, 2));

        Map<String, Integer> result = EntryZtream.of(withNulls.stream().parallel()).toMap();

        assertEquals(2, result.size());
        assertEquals(Integer.valueOf(2), result.get(null));
        assertTrue(result.containsKey("a"));
        assertNull(result.get("a"));
    }
}

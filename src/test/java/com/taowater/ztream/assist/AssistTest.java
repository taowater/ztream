package com.taowater.ztream.assist;

import com.taowater.ztream.Any;
import com.taowater.ztream.op.math.Peak;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AssistTest {

    @Test
    void indexedFunctionsIncrementFromZeroAndBooleanWrapperUnboxesSafely() {
        List<String> consumed = new ArrayList<>();
        Functions.IndexedConsumer<String> consumer = new Functions.IndexedConsumer<>(
                (value, index) -> consumed.add(value + index));
        Functions.IndexedFunction<String, String> function = new Functions.IndexedFunction<>(
                (value, index) -> value + index);

        consumer.accept("a");
        consumer.accept("b");

        assertEquals(Arrays.asList("a0", "b1"), consumed);
        assertEquals("a0", function.apply("a"));
        assertEquals("b1", function.apply("b"));
        assertTrue(Functions.of(v -> true).test("x"));
        assertFalse(Functions.of(v -> null).test("x"));
    }

    @Test
    void safePredicateRejectsNullAndPreservesResults() {
        Predicate<String> nonEmpty = value -> !value.isEmpty();
        Predicate<? super String> safe = Functions.safe(nonEmpty);

        assertFalse(safe.test(null));
        assertFalse(safe.test(""));
        assertTrue(safe.test("x"));
        Predicate<String> throwing = new ThrowingPredicate();
        assertThrows(RuntimeException.class, () -> Functions.safe(throwing).test("x"));
    }

    @Test
    void entryHelpersAreNullSafeAndTransformBothSides() {
        Map.Entry<String, Integer> entry = Functions.entry("ab", v -> v.substring(0, 1), String::length);

        assertEquals(new SimpleEntry<>("a", 2), entry);
        assertEquals(new SimpleEntry<>(null, null),
                Functions.entry(null, Object::toString, Object::hashCode));
        assertEquals(new SimpleEntry<>("A", 20),
                Functions.entryKeyValue(entry, String::toUpperCase, v -> v * 10));
        assertEquals(new SimpleEntry<>(2, "a"), Functions.flip(entry));
    }

    @Test
    void boxesStoreValuesAndPairEqualityUsesSecondValue() {
        Box<String> box = new Box<>();
        box.accept("x");
        Box.PairBox<String, Integer> pair = new Box.PairBox<>("first", 1);

        assertEquals("x", box.getA());
        assertEquals("first", pair.getA());
        assertEquals(Integer.valueOf(1), pair.getB());
        assertEquals(new Box.PairBox<>("other", 1), pair);
        assertNotEquals(new Box.PairBox<>("first", 2), pair);
        assertNotEquals(pair, null);
        assertNotEquals(pair, "pair");
        assertEquals(Integer.valueOf(1).hashCode(), pair.hashCode());
        assertEquals(new Box.PairBox<>("same", "same"), Box.PairBox.single("same"));
    }

    @Test
    void collectorImplementationExposesItsComponentsAndComputesAverage() {
        ExCollectors.CollectorImpl<Integer, List<Integer>, Integer> collector =
                ExCollectors.avg(v -> v, false);
        List<Integer> left = collector.supplier().get();
        collector.accumulator().accept(left, 2);
        List<Integer> right = collector.supplier().get();
        collector.accumulator().accept(right, 4);

        assertSame(left, collector.combiner().apply(left, right));
        assertEquals(Integer.valueOf(3), collector.finisher().apply(left));
        assertTrue(collector.characteristics().isEmpty());
        assertNull(Stream.<Integer>empty().collect(ExCollectors.avg(v -> v, false)));
        assertEquals(Integer.valueOf(2), Stream.of(2, null, 4)
                .collect(ExCollectors.avg(v -> v, true)));
    }

    @Test
    void peakAndJoinCollectorsSupportParallelCombinationAndNulls() {
        Peak<Any<Integer>> peakByComparator = Stream.of(3, 1, 2).parallel()
                .collect(ExCollectors.peak(Comparator.naturalOrder(), true));
        Peak<Any<String>> peakByProperty = Stream.of("a", "ccc", "bb").parallel()
                .collect(ExCollectors.peak(String::length, true));

        assertEquals(Integer.valueOf(3), peakByComparator.getMax().get());
        assertEquals(Integer.valueOf(1), peakByComparator.getMin().get());
        assertEquals("ccc", peakByProperty.getMax().get());
        assertEquals("a", peakByProperty.getMin().get());
        assertEquals("a,null,b", Stream.of("a", null, "b").collect(ExCollectors.join(",")));
        assertEquals("[a|b]", Stream.of("a", "b")
                .collect(ExCollectors.join("|", "[", "]")));
    }

    @Test
    void groupingAndMappingCollectorsHandleNullAndBothFinisherKinds() {
        Map<Integer, List<String>> lists = Stream.of("a", "bb", null).parallel().collect(
                ExCollectors.groupingBy(String::length, HashMap::new, Collectors.toList()));
        Map<Integer, Long> counts = Stream.of("a", "bb", null).parallel().collect(
                ExCollectors.groupingBy(String::length, HashMap::new, Collectors.counting()));
        Collector<String, ?, List<Integer>> mapping = ExCollectors.mapping(
                String::length, Collectors.toList());

        assertEquals(Arrays.asList("a"), lists.get(1));
        assertEquals(Arrays.asList((String) null), lists.get(null));
        assertEquals(Long.valueOf(1), counts.get(null));
        assertEquals(Arrays.asList(1, null), Stream.of("a", null).collect(mapping));
    }

    @Test
    void groupSpliteratorBuildsGroupsAndReportsConservativeCharacteristics() {
        Spliterators.GroupSpliterator<String, Integer, String, ?, List<String>> spliterator =
                new Spliterators.GroupSpliterator<>(Arrays.asList("a", "bb", null).spliterator(),
                        String::length, value -> value, Collectors.toList());
        long originalEstimate = spliterator.estimateSize();
        List<Map.Entry<Integer, List<String>>> groups = new ArrayList<>();

        while (spliterator.tryAdvance(groups::add)) {
            // consume all groups
        }

        assertEquals(3, originalEstimate);
        assertEquals(3, groups.stream().mapToInt(entry -> entry.getValue().size()).sum());
        assertNull(spliterator.trySplit());
        assertEquals(0, spliterator.characteristics() & Spliterator.SIZED);
    }

    @Test
    void appendSpliteratorCoversAdvanceRemainingSplitAndSizeStates() {
        Spliterators.AppendSpliterator<Integer> advance = new Spliterators.AppendSpliterator<>(
                Arrays.asList(1).spliterator(), Arrays.asList(2).spliterator());
        List<Integer> values = new ArrayList<>();

        assertEquals(2, advance.estimateSize());
        assertTrue(advance.tryAdvance(values::add));
        assertTrue(advance.tryAdvance(values::add));
        assertFalse(advance.tryAdvance(values::add));
        assertEquals(Arrays.asList(1, 2), values);

        Spliterators.AppendSpliterator<Integer> remaining = new Spliterators.AppendSpliterator<>(
                Arrays.asList(1).spliterator(), Arrays.asList(2, 3).spliterator());
        values.clear();
        remaining.forEachRemaining(values::add);
        assertEquals(Arrays.asList(1, 2, 3), values);

        Spliterators.AppendSpliterator<Integer> split = new Spliterators.AppendSpliterator<>(
                Arrays.asList(1).spliterator(), Arrays.asList(2, 3).spliterator());
        assertNotNull(split.trySplit());
        assertNotNull(split.trySplit());
        assertTrue((split.characteristics() & Spliterator.ORDERED) != 0);
    }

    @Test
    void appendSpliteratorProtectsAgainstSizeOverflow() {
        Spliterator<Integer> huge = new AbstractSpliterator<Integer>(Long.MAX_VALUE, Spliterator.SIZED) {
            @Override
            public boolean tryAdvance(java.util.function.Consumer<? super Integer> action) {
                return false;
            }
        };
        Spliterators.AppendSpliterator<Integer> append = new Spliterators.AppendSpliterator<>(
                huge, Collections.singletonList(1).spliterator());

        assertEquals(Long.MAX_VALUE, append.estimateSize());
        assertEquals(0, append.characteristics() & Spliterator.SIZED);
    }

    public static class ThrowingPredicate implements Predicate<String> {
        @Override
        public boolean test(String value) {
            throw new IllegalArgumentException("bad value");
        }
    }
}

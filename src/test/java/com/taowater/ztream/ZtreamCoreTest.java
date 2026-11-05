package com.taowater.ztream;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

class ZtreamCoreTest {

    @Test
    void factoriesHandleEverySupportedSource() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        assertTrue(Ztream.empty().toList().isEmpty());
        assertTrue(Ztream.of((Object[]) null).toList().isEmpty());
        assertEquals(Arrays.asList(1, 2), Ztream.of(1, 2).toList());
        assertEquals(Arrays.asList(1, 2), Ztream.of(Arrays.asList(1, 2)).toList());
        assertTrue(Ztream.of((Iterable<Object>) null).toList().isEmpty());
        assertTrue(Ztream.of(Arrays.asList(1, 2), true).isParallel());
        assertEquals(Arrays.asList(1, 2), Ztream.of(Stream.of(1, 2)).toList());
        assertTrue(Ztream.of((Stream<Object>) null).toList().isEmpty());
        assertEquals(map, Ztream.of(map).toMap());
        assertTrue(Ztream.of((Map<Object, Object>) null).toMap().isEmpty());
    }

    @Test
    void splitSupportsAllOverloadsAndNull() {
        assertEquals(Arrays.asList("a", "b"), Ztream.split("a,b,a").toList());
        assertEquals(Arrays.asList("a", "b", "a"), Ztream.split("a,b,a", false).toList());
        assertEquals(Arrays.asList("a", "b"), Ztream.split("a|b|a", "\\|").toList());
        assertEquals(Arrays.asList("a", "b", "a"), Ztream.split("a|b|a", "\\|", false).toList());
        assertEquals(Arrays.asList(" a", "b", "a "), Ztream.split(" a,b,a ").toList());
        assertTrue(Ztream.split(null).toList().isEmpty());
    }

    @Test
    void mappingAndPrimitiveMappingDelegateToTheUnderlyingStream() {
        assertEquals(Arrays.asList("1", "2"), Ztream.of(1, 2).map(e -> String.valueOf(e)).toList());
        assertEquals(Arrays.asList("a", "A", "b", "B"),
                Ztream.of("a", "b").map(v -> v, String::toUpperCase).toList());
        assertEquals(Arrays.asList("a0", "b1"),
                Ztream.of("a", "b").map((value, index) -> value + index).toList());
        assertEquals(Arrays.asList(1, 11, 2, 12),
                Ztream.of(1, 2).flatMap(v -> Stream.of(v, v + 10)).toList());
        assertArrayEquals(new int[]{2, 4}, Ztream.of(1, 2).mapToInt(v -> v * 2).toArray());
        assertArrayEquals(new long[]{2, 4}, Ztream.of(1, 2).mapToLong(v -> v * 2L).toArray());
        assertArrayEquals(new double[]{0.5, 1.0}, Ztream.of(1, 2).mapToDouble(v -> v / 2.0).toArray());
        assertArrayEquals(new int[]{1, 2, 2, 3},
                Ztream.of(1, 2).flatMapToInt(v -> IntStream.of(v, v + 1)).toArray());
        assertArrayEquals(new long[]{1, 2, 2, 3},
                Ztream.of(1, 2).flatMapToLong(v -> LongStream.of(v, v + 1)).toArray());
        assertArrayEquals(new double[]{1, 1.5, 2, 2.5},
                Ztream.of(1, 2).flatMapToDouble(v -> DoubleStream.of(v, v + 0.5)).toArray());
    }

    @Test
    void standardIntermediateOperationsRetainEnhancedStreamType() {
        List<Integer> peeked = new ArrayList<>();

        List<Integer> result = Ztream.of(3, 1, 2, 2)
                .filter(v -> v > 1)
                .distinct()
                .sorted()
                .peek((Consumer<Integer>) peeked::add)
                .skip(1)
                .limit(1)
                .toList();

        assertEquals(Arrays.asList(3), result);
        assertEquals(Arrays.asList(2, 3), peeked);
        assertEquals(Arrays.asList(3, 2, 1),
                Ztream.of(1, 2, 3).sorted(java.util.Comparator.reverseOrder()).toList());
    }

    @Test
    void terminalOperationsMatchJavaStreamSemantics() {
        assertArrayEquals(new Object[]{1, 2}, Ztream.of(1, 2).toArray());
        assertArrayEquals(new Integer[]{1, 2}, Ztream.of(1, 2).toArray(Integer[]::new));
        assertEquals(6, Ztream.of(1, 2, 3).reduce(0, Integer::sum));
        assertEquals(6, Ztream.of(1, 2, 3).reduce(Integer::sum).orElse(-1));
        assertEquals("123", Ztream.of(1, 2, 3).reduce("", (s, v) -> s + v, String::concat));
        assertEquals(Arrays.asList(1, 2), Ztream.of(1, 2).collect(
                ArrayList::new, List::add, List::addAll));
        assertEquals(Arrays.asList(1, 2), Ztream.of(1, 2).collect(Collectors.toList()));
        assertEquals(1, Ztream.of(3, 1, 2).min(Integer::compareTo).orElse(-1));
        assertEquals(3, Ztream.of(3, 1, 2).max(Integer::compareTo).orElse(-1));
        assertEquals(3, Ztream.of(1, 2, 3).count());
        assertTrue(Ztream.of(1, 2).anyMatch(v -> v == 2));
        assertTrue(Ztream.of(1, 2).allMatch(v -> v > 0));
        assertTrue(Ztream.of(1, 2).noneMatch(v -> v < 0));
        assertEquals(1, Ztream.of(1, 2).findFirst().orElse(-1));
        assertTrue(Ztream.of(1).findAny().isPresent());
    }

    @Test
    void nullTolerantFindMethodsCanSuppressOrRethrowNpe() {
        assertFalse(Ztream.of((Integer) null, 1).findFirst(false).isPresent());
        assertThrows(NullPointerException.class,
                () -> Ztream.of((Integer) null, 1).findFirst(true));
        assertFalse(Ztream.of((Integer) null).findAny(false).isPresent());
        assertThrows(NullPointerException.class,
                () -> Ztream.of((Integer) null).findAny(true));
    }

    @Test
    void iterationParallelModesAndCloseHandlerAreExposed() {
        Iterator<Integer> iterator = Ztream.of(1, 2).iterator();
        Spliterator<Integer> spliterator = Ztream.of(1, 2).spliterator();
        List<Integer> ordered = new ArrayList<>();
        AtomicBoolean closed = new AtomicBoolean();

        assertEquals(1, iterator.next());
        assertEquals(2, spliterator.getExactSizeIfKnown());
        Ztream.of(1, 2).parallel().forEachOrdered(ordered::add);
        assertEquals(Arrays.asList(1, 2), ordered);
        assertTrue(Ztream.of(1).parallel().isParallel());
        assertFalse(Ztream.of(1).parallel().sequential().isParallel());
        assertEquals(2, Ztream.of(1, 2).unordered().count());

        Ztream<Integer> closeable = Ztream.of(1).onClose(() -> closed.set(true));
        closeable.close();
        assertTrue(closed.get());
    }

    @Test
    void firstAnyLastAndRandomHandleEmptyAndPopulatedStreams() {
        assertEquals(1, Ztream.of(1, 2, 3).first().get());
        assertEquals(1, Ztream.of(1, 2, 3).getFirst());
        assertTrue(Ztream.of(1, 2, 3).any().isPresent());
        assertEquals(3, Ztream.of(1, 2, 3).last().get());
        assertEquals(3, Ztream.of(1, 2, 3).getLast());
        assertTrue(Arrays.asList(1, 2, 3).contains(Ztream.of(1, 2, 3).random().get()));
        assertNull(Ztream.empty().getFirst());
        assertNull(Ztream.empty().getLast());
        assertTrue(Ztream.empty().random().isNull());
    }

    @Test
    void indexedTraversalAppendFlatCastPageAndRangesCoverConvenienceMethods() {
        List<String> indexed = new ArrayList<>();
        List<String> peeked = new ArrayList<>();
        Ztream.of("a", "b").forEach((value, index) -> indexed.add(value + index));
        List<String> values = Ztream.of("a", "b")
                .peek((value, index) -> peeked.add(value + index)).toList();

        assertEquals(Arrays.asList("a0", "b1"), indexed);
        assertEquals(indexed, peeked);
        assertEquals(Arrays.asList("a", "b"), values);
        assertEquals(Arrays.asList(1, 2), Ztream.of(1).append(2).toList());
        assertEquals(Arrays.asList(1), Ztream.of(1).append(new Integer[0]).toList());
        assertEquals(Arrays.asList(1, 2, 3), Ztream.of(1).append(Arrays.asList(2, 3)).toList());
        assertEquals(Arrays.asList(1, 2), Ztream.of(1).append(Arrays.spliterator(new Integer[]{2})).toList());
        assertEquals(Arrays.asList(1), Ztream.of(1)
                .append(java.util.Spliterators.<Integer>emptySpliterator()).toList());
        assertEquals(Arrays.asList(1), Ztream.of(1).append((Spliterator<Integer>) null).toList());
        assertEquals(Arrays.asList(1), Ztream.of(1).append((Iterable<Integer>) null).toList());
        assertEquals(Arrays.asList(1, 2, 3),
                Ztream.of(1).append(Arrays.asList("2", "3"), Integer::valueOf).toList());
        assertThrows(NullPointerException.class,
                () -> Ztream.of(1).append(Arrays.asList("2"), null));
        assertEquals(Arrays.asList(1),
                Ztream.of(1).append((Iterable<String>) null, Integer::valueOf).toList());
        assertEquals(2, Ztream.of(4, 5, 6).firstIdx(v -> v == 6));
        assertEquals(-1, Ztream.of(4, 5, 6).firstIdx(v -> false));
        assertEquals(Arrays.asList(1, 2, 3),
                Ztream.of(Arrays.asList(1, 2), Arrays.asList(3)).flat(v -> v).toList());
        assertEquals(Arrays.asList(1, 2), Ztream.<Number>of(1, 2).cast(Integer.class).toList());
        assertEquals(Arrays.asList(3, 4), Ztream.range(1, 6).page(2, 2).toList());
        assertEquals(Arrays.asList(1, 2), Ztream.range(1, 3).toList());
        assertEquals(Arrays.asList(1, 2, 3), Ztream.range(1, 3, true).toList());
        assertEquals(Arrays.asList(1L, 2L), Ztream.range(1L, 3L).toList());
        assertEquals(Arrays.asList(1L, 2L, 3L), Ztream.range(1L, 3L, true).toList());
    }

    @Test
    void conversionOverloadsTransformAndExposeSourceAndTarget() {
        AtomicReference<String> context = new AtomicReference<>();
        TestFixtures.Student student = TestFixtures.student("Ada", 12);

        assertEquals("Ada", Ztream.of(student).convert(TestFixtures.Teacher.class).getFirst().getName());
        assertEquals(Arrays.asList(2, 3), Ztream.of("1", "2")
                .convert(Integer.class, (source, target) -> Integer.valueOf(source) + 1).toList());
        assertEquals("Ada", Ztream.of(student).convert(TestFixtures.Teacher.class,
                        (BiConsumer<TestFixtures.Student, TestFixtures.Teacher>)
                                (source, result) -> context.set(source.getName() + ":" + result.getName()))
                .getFirst().getName());
        assertEquals("Ada:Ada", context.get());
        assertEquals(Arrays.asList(3), Ztream.of("2").convert(Integer.class,
                (source, target) -> Integer.valueOf(source) + 1,
                (source, result) -> context.set(source + ":" + result)).toList());
        assertEquals("2:3", context.get());
    }
}

package com.taowater.ztream;

import com.taowater.taol.core.function.Function2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class AnyTest {

    @Test
    void factoriesAndStateHandleNullAndBlankStrings() {
        assertSame(Any.empty(), Any.of((Object) null));
        assertSame(Any.empty(), Any.of(""));
        assertTrue(Any.of(" ").isPresent());
        assertTrue(Any.empty().isNull());
        assertTrue(Any.of(new ArrayList<>()).isEmpty());
        assertFalse(Any.of(Arrays.asList(1)).isEmpty());
    }

    @Test
    void getAndFallbackMethodsCoverPresentAndEmptyValues() {
        Any<String> value = Any.of("value");
        Any<String> empty = Any.empty();

        assertEquals("value", value.get());
        assertEquals(5, value.get(String::length, -1));
        assertEquals(-1, value.get(v -> null, -1));
        assertEquals(-1, empty.get(String::length, -1));
        assertEquals("fallback", empty.orElse("fallback"));
        assertEquals("supplied", empty.orElseGet(() -> "supplied"));
        assertEquals("value", value.orElseGet(() -> "unused"));
        assertEquals("value", value.orElseThrow());
        assertEquals("value", value.orElseThrow(IllegalStateException::new));
        assertThrows(NoSuchElementException.class, empty::get);
        assertThrows(NoSuchElementException.class, empty::orElseThrow);
        assertThrows(IllegalStateException.class,
                () -> empty.orElseThrow(() -> new IllegalStateException("missing")));
    }

    @Test
    void consumersRunOnlyForApplicableValues() {
        List<String> seen = new ArrayList<>();
        AtomicBoolean emptyAction = new AtomicBoolean();
        Any<String> value = Any.of("x");

        assertSame(value, value.ifPresent(seen::add));
        Any.<String>empty().ifPresent(seen::add);
        Any.of("x").ifPresent(String::toUpperCase, seen::add, seen::add);
        Any.<String>empty().ifPresent((Function<String, String>) v -> null, seen::add);
        Any.of("x").ifPresentOrElse(seen::add, () -> emptyAction.set(true));
        Any.<String>empty().ifPresentOrElse(seen::add, () -> emptyAction.set(true));

        assertEquals(Arrays.asList("x", "X", "X", "x"), seen);
        assertTrue(emptyAction.get());
    }

    @Test
    void filterMapFlatMapAndOrAreLazyForEmptyValues() {
        AtomicBoolean supplierCalled = new AtomicBoolean();
        Any<Integer> value = Any.of(4);

        assertSame(value, value.filter(v -> v % 2 == 0));
        assertFalse(value.filter(v -> false).isPresent());
        assertSame(Any.empty(), Any.empty().filter(v -> fail("predicate must not run")));
        assertEquals(8, value.map(v -> v * 2).get());
        assertTrue(Any.empty().map(v -> fail("mapper must not run")).isNull());
        assertEquals(5, value.flatMap(v -> Any.of(v + 1)).get());
        assertTrue(Any.empty().flatMap(v -> Any.of(1)).isNull());
        assertSame(value, value.or(() -> {
            supplierCalled.set(true);
            return Any.of(9);
        }));
        assertFalse(supplierCalled.get());
        assertEquals(9, Any.<Integer>empty().or(() -> Any.of(9)).get());

        assertThrows(NullPointerException.class, () -> value.map(null));
        assertThrows(NullPointerException.class, () -> value.flatMap(v -> null));
        assertThrows(NullPointerException.class, () -> Any.empty().or(null));
    }

    @Test
    void ztreamAdaptersExposeZeroOneOrManyValues() {
        assertEquals(Arrays.asList(1, 2), Any.of(Arrays.asList(1, 2)).ztream(v -> v).toList());
        assertEquals(Arrays.asList("x"), Any.of("x").ztream().toList());
        assertTrue(Any.empty().ztream().toList().isEmpty());
    }

    @Test
    void conversionCastAndPeekReturnExpectedValues() {
        AtomicReference<String> conversion = new AtomicReference<>();
        AtomicReference<Number> peeked = new AtomicReference<>();
        TestFixtures.Student student = TestFixtures.student("Ada", 12);

        Any<Integer> converted = Any.of("12").convert(Integer.class,
                (source, target) -> Integer.valueOf(source),
                (source, result) -> conversion.set(source + ":" + result));
        Any<Integer> convertedWithoutConsumer = Any.of("13").convert(Integer.class,
                (Function2<String, Class<Integer>, Integer>)
                        (source, target) -> Integer.valueOf(source));
        Any<TestFixtures.Teacher> convertedWithConsumer = Any.of(student).convert(TestFixtures.Teacher.class,
                (BiConsumer<TestFixtures.Student, TestFixtures.Teacher>)
                        (source, result) -> conversion.set(source.getName() + ":" + result.getName()));

        assertEquals(12, converted.get());
        assertEquals(13, convertedWithoutConsumer.get());
        assertEquals("Ada", convertedWithConsumer.get().getName());
        assertEquals("Ada:Ada", conversion.get());
        assertEquals("Ada", Any.of(student).get(TestFixtures.Teacher.class).getName());
        assertEquals(3, Any.<Number>of(3).cast(Integer.class).peek(peeked::set).get());
        Any.<Number>empty().peek(v -> fail("empty value must not be consumed"));
        assertEquals(3, peeked.get());
    }

    @Test
    void objectContractIsValueBased() {
        Any<Integer> value = Any.of(1);

        assertEquals(value, value);
        assertEquals(value, Any.of(1));
        assertNotEquals(value, Any.of(2));
        assertNotEquals(value, "1");
        assertEquals(Integer.valueOf(1).hashCode(), value.hashCode());
        assertEquals("1", value.toString());
        assertNull(Any.empty().toString());
    }
}

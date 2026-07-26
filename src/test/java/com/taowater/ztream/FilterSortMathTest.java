package com.taowater.ztream;

import com.taowater.taol.core.function.Function1;
import com.taowater.ztream.op.math.Peak;
import com.taowater.ztream.op.filter.Wrapper;
import com.taowater.ztream.op.sort.Sorter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class FilterSortMathTest {

    @Test
    void comparisonFiltersCoverCollectionsVarargsAndProperties() {
        assertEquals(Arrays.asList("bb"),
                Ztream.of("a", "bb", null).filter(String::length, v -> v != null && v == 2).toList());
        assertEquals(Arrays.asList("a"), Ztream.of("a", "bb").eq(String::length, 1).toList());
        assertEquals(Arrays.asList("a", "ccc"), Ztream.of("a", "bb", "ccc")
                .in(String::length, Arrays.asList(1, 3)).toList());
        assertEquals(Arrays.asList("bb"), Ztream.of("a", "bb")
                .in(String::length, 2, 4).toList());
        assertEquals(Arrays.asList("a", "c"), Ztream.of("a", "b", "c")
                .in(Arrays.asList("a", "c")).toList());
        assertEquals(Arrays.asList("a", "c"), Ztream.of("a", "b", "c").in("a", "c").toList());
        assertEquals(Arrays.asList("b"), Ztream.of("a", "b", "c")
                .notIn(Arrays.asList("a", "c")).toList());
        assertEquals(Arrays.asList("b"), Ztream.of("a", "b", "c").notIn("a", "c").toList());
        assertEquals(Arrays.asList("bb"), Ztream.of("a", "bb")
                .notIn(String::length, Arrays.asList(1)).toList());
        assertEquals(Arrays.asList("a"), Ztream.of("a", "bb")
                .notIn(String::length, 2, 3).toList());
    }

    @Test
    void nullEmptyBlankNumericAndLikeFiltersCoverBoundaryValues() {
        List<String> text = Arrays.asList(null, "", " ", "alpha", "beta");
        Function<String, String> identity = Function.identity();
        Function<String, CharSequence> charSequence = value -> value;

        assertEquals(Arrays.asList((String) null), Ztream.of(text).isNull().toList());
        assertEquals(Arrays.asList("", " ", "alpha", "beta"), Ztream.of(text).nonNull().toList());
        assertEquals(Arrays.asList(null, ""), Ztream.of(text).isEmpty(identity).toList());
        assertEquals(Arrays.asList(" ", "alpha", "beta"), Ztream.of(text).nonEmpty(identity).toList());
        assertEquals(Arrays.asList(null, "", " "), Ztream.of(text).isBlank(charSequence).toList());
        assertEquals(Arrays.asList("alpha", "beta"), Ztream.of(text).nonBlank(charSequence).toList());
        assertEquals(Arrays.asList((String) null), Ztream.of(text).isNull(String::isEmpty).toList());
        assertEquals(Arrays.asList("", " ", "alpha", "beta"),
                Ztream.of(text).nonNull(String::isEmpty).toList());
        assertEquals(Arrays.asList(1, 2), Ztream.of(null, 1, 2, 3).lt(Function.identity(), 3).toList());
        assertEquals(Arrays.asList(1, 2, 3), Ztream.of(null, 1, 2, 3).le(Function.identity(), 3).toList());
        assertEquals(Arrays.asList(3), Ztream.of(null, 1, 2, 3).gt(Function.identity(), 2).toList());
        assertEquals(Arrays.asList(2, 3), Ztream.of(null, 1, 2, 3).ge(Function.identity(), 2).toList());
        assertEquals(Arrays.asList(2, 3), Ztream.of(null, 1, 2, 3, 4)
                .between(Function.identity(), 2, 3).toList());
        assertEquals(Arrays.asList(null, 1, 2), Ztream.of(null, 1, 2)
                .between(Function.identity(), null, null).toList());
        assertEquals(Arrays.asList(1, 2), Ztream.of(null, 1, 2, 3)
                .between(Function.identity(), null, 2).toList());
        assertEquals(Arrays.asList("alpha"), Ztream.of(text).rightLike(identity, "al").toList());
        assertEquals(Arrays.asList("alpha", "beta"), Ztream.of(text).like(identity, "a").toList());
        assertTrue(Ztream.of("alpha").rightLike(identity, null).toList().isEmpty());
        assertTrue(Ztream.of("alpha").like(identity, null).toList().isEmpty());
    }

    @Test
    void conditionalAndXFiltersEitherApplyOrPreserveTheStream() {
        assertEquals(Arrays.asList(1, 2, 3), Ztream.of(1, 2, 3).filter(false, v -> false).toList());
        assertEquals(Arrays.asList(1, 2, 3), Ztream.of(1, 2, 3)
                .eqX(Function.identity(), null).toList());
        assertEquals(Arrays.asList(2), Ztream.of(1, 2, 3)
                .inX(Function.identity(), Arrays.asList(2)).toList());
        assertEquals(Arrays.asList(2), Ztream.of(1, 2, 3).inX(Function.identity(), 2).toList());
        assertEquals(Arrays.asList(2), Ztream.of(1, 2, 3).inX(Arrays.asList(2)).toList());
        assertEquals(Arrays.asList(2), Ztream.of(1, 2, 3).inX(2).toList());
        assertEquals(Arrays.asList(1, 3), Ztream.of(1, 2, 3).notInX(Arrays.asList(2)).toList());
        assertEquals(Arrays.asList(1, 3), Ztream.of(1, 2, 3).notInX(2).toList());
        assertEquals(Arrays.asList(1, 3), Ztream.of(1, 2, 3)
                .notInX(Function.identity(), Arrays.asList(2)).toList());
        assertEquals(Arrays.asList(1, 3), Ztream.of(1, 2, 3)
                .notInX(Function.identity(), 2).toList());
        assertEquals(Arrays.asList(1), Ztream.of(1, 2, 3).ltX(Function.identity(), 2).toList());
        assertEquals(Arrays.asList(1, 2), Ztream.of(1, 2, 3).leX(Function.identity(), 2).toList());
        assertEquals(Arrays.asList(3), Ztream.of(1, 2, 3).gtX(Function.identity(), 2).toList());
        assertEquals(Arrays.asList(2, 3), Ztream.of(1, 2, 3).geX(Function.identity(), 2).toList());
        assertEquals(Arrays.asList(2), Ztream.of(1, 2, 3)
                .betweenX(Function.identity(), 2, 2).toList());
        assertEquals(Arrays.asList("alpha"), Ztream.of("alpha", "beta")
                .rightLikeX(Function.identity(), "al").toList());
        assertEquals(Arrays.asList("alpha", "beta"), Ztream.of("alpha", "beta")
                .likeX(Function.identity(), "a").toList());
        assertEquals(Arrays.asList("alpha", "beta"), Ztream.of("alpha", "beta")
                .likeX(Function.identity(), "").toList());
    }

    @Test
    void wrapperCompositionAndBooleanFiltersHaveExplicitAssertions() {
        Wrapper<Integer> disabled = new Wrapper<>();
        disabled.filter(false, value -> false);
        assertTrue(disabled.getCondition().test(1));

        assertEquals(Arrays.asList(2, 3), Ztream.of(1, 2, 3, 4).query(wrapper -> wrapper
                .gt(Function.identity(), 1)
                .and(inner -> inner.lt(Function.identity(), 4))
                .or(inner -> inner.eq(Function.identity(), 3))).toList());
        assertEquals(Arrays.asList(2), Ztream.of(1, 2, null)
                .isTrue(v -> v == null ? null : v % 2 == 0).toList());
        assertEquals(Arrays.asList(1, null), Ztream.of(1, 2, null)
                .isFalse(v -> v == null ? null : v % 2 == 0).toList());
    }

    @Test
    void naturalKeyAndComparatorSortingCoverNullOrderAndConditions() {
        assertEquals(Arrays.asList(null, 1, 2), Ztream.of(2, null, 1).asc().toList());
        assertEquals(Arrays.asList(null, 1, 2), Ztream.of(2, null, 1).asc(false).toList());
        assertEquals(Arrays.asList(null, 2, 1), Ztream.of(1, null, 2).desc().toList());
        assertEquals(Arrays.asList(null, 2, 1), Ztream.of(1, null, 2).desc(false).toList());
        assertEquals(Arrays.asList(null, "a", "bb"), Ztream.of("bb", null, "a")
                .asc(String::length).toList());
        assertEquals(Arrays.asList(null, "bb", "a"), Ztream.of("a", null, "bb")
                .desc(String::length, false).toList());
        assertEquals(Arrays.asList(null, "a", "bb"), Ztream.of("bb", null, "a")
                .asc(Comparator.comparingInt(String::length)).toList());
        assertEquals(Arrays.asList(null, "bb", "a"), Ztream.of("a", null, "bb")
                .desc(Comparator.comparingInt(String::length), false).toList());
        assertEquals(Arrays.asList(2, 1), Ztream.of(2, 1)
                .asc(false, Function.identity(), true).toList());
        assertEquals(Arrays.asList(2, 1), Ztream.of(2, 1)
                .desc(false, Comparator.naturalOrder(), true).toList());
        assertEquals(Arrays.asList(2, 1), Ztream.of(2, 1)
                .asc(false, Comparator.naturalOrder()).toList());
        assertEquals(Arrays.asList(2, 1), Ztream.of(2, 1)
                .desc(false, Comparator.naturalOrder()).toList());
        assertEquals(Arrays.asList(2, 1), Ztream.of(1, 2)
                .desc(Comparator.naturalOrder()).toList());
    }

    @Test
    void reverseShuffleAndSorterCompositionPreserveElements() {
        List<Integer> shuffled = Ztream.of(1, 2, 3, 4).shuffle().toList();
        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4)), new HashSet<>(shuffled));
        assertEquals(Arrays.asList(1, 2, 3), Ztream.of(1, 2, 3).shuffle(false).toList());
        assertEquals(Arrays.asList(3, 2, 1), Ztream.of(1, 2, 3).reverse().toList());
        assertEquals(Arrays.asList(1, 2, 3), Ztream.of(1, 2, 3).reverse(false).toList());

        Sorter<String> sorter = new Sorter<>(false);
        sorter.sort(false, String::length, false, true)
                .sort(String::length, false, true)
                .sort(Function.identity(), Comparator.naturalOrder())
                .then(Function.identity(), Comparator.naturalOrder())
                .nullFirst(false);
        List<String> values = new ArrayList<>(Arrays.asList("bb", "a", "aa", null));
        values.sort(sorter.getComparator());
        assertEquals(Arrays.asList("a", "aa", "bb", null), values);
    }

    @Test
    void mathOperationsCoverDefaultsNullCountingAndPeakResults() {
        Function1<Integer, Integer> number = value -> value;

        assertEquals(Integer.valueOf(6), Ztream.of(1, null, 2, 3).sum(number));
        assertEquals(Integer.valueOf(9), Ztream.<Integer>empty().sum(number, 9));
        assertEquals(Integer.valueOf(3), Ztream.of(1, 3, 2).max(number));
        assertEquals(Integer.valueOf(1), Ztream.of(1, 3, 2).min(number));
        assertEquals(Integer.valueOf(9), Ztream.<Integer>empty().max(number, 9));
        assertEquals(Integer.valueOf(9), Ztream.<Integer>empty().min(number, 9));
        assertEquals(Integer.valueOf(3), Ztream.of(1, 3, 2).max().get());
        assertEquals(Integer.valueOf(1), Ztream.of(1, 3, 2).min().get());
        assertEquals(Integer.valueOf(3), Ztream.of(1, 3, 2).maxBy(number).get());
        assertEquals(Integer.valueOf(1), Ztream.of(1, 3, 2).minBy(number, false).get());
        assertEquals(Integer.valueOf(2), Ztream.of(2, null, 4)
                .avg(number, -1, true));
        assertEquals(Integer.valueOf(3), Ztream.of(2, null, 4)
                .avg(number, -1, false));
        assertEquals(Integer.valueOf(-1), Ztream.<Integer>empty()
                .avg(number, -1));

        Peak<Integer> peak = new Peak<>(3, 1);
        assertEquals(Integer.valueOf(3), peak.getMax());
        peak.setMin(0);
        assertEquals(Integer.valueOf(0), peak.getMin());
        assertEquals(new Peak<>(3, 0), peak);
    }

    @Test
    void nonComparableValuesUseStringRepresentationForPeaks() {
        PlainValue a = new PlainValue("a");
        PlainValue b = new PlainValue("b");

        assertSame(b, Ztream.of(a, b).max().get());
        assertSame(a, Ztream.of(a, b).min(false).get());
    }

    private static final class PlainValue {
        private final String value;

        private PlainValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}

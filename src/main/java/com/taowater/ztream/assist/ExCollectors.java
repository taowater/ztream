package com.taowater.ztream.assist;


import com.taowater.ztream.Any;
import com.taowater.ztream.Ztream;
import com.taowater.ztream.op.math.BigDecimalStrategy;
import com.taowater.ztream.op.math.Peak;
import com.taowater.ztream.op.sort.Sorter;
import io.vavr.Function1;
import lombok.experimental.UtilityClass;
import org.dromara.hutool.core.math.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * 自定义收集器
 *
 * @author Zhu56
 * @date 2023/04/23 21:58:06
 */
@UtilityClass
@SuppressWarnings("unchecked")
public class ExCollectors {

    /**
     * 收集器实现
     * Collectors.CollectorImpl 不给我用我不会抄一个过来吗
     */
    public static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Function<A, R> finisher,
                      Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        @SuppressWarnings("unchecked")
        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, i -> (R) i, characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }

    /**
     * 平均值收集器
     *
     * @param fun       属性
     * @param nullCount null是否计数
     * @return {@link CollectorImpl }<{@link T }, {@link List }<{@link T }>, {@link N }>
     */
    public static <T, N extends Number> CollectorImpl<T, List<T>, N> avg(Function1<? super T, N> fun, boolean nullCount) {
        return new CollectorImpl<>(
                ArrayList::new,
                List::add,
                (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                },
                list -> {
                    N sum = Ztream.of(list).sum(fun);
                    long count = nullCount ? Ztream.of(list).count() : Ztream.of(list).nonNull().map(fun).nonNull().count();
                    if (Objects.isNull(sum) || count == 0) {
                        return null;
                    }
                    BigDecimal avgValue = NumberUtil.toBigDecimal(sum).divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
                    return BigDecimalStrategy.getValue(avgValue, fun);
                },
                Collections.emptySet()
        );
    }

    /**
     * 最值收集器
     *
     * @param comparator 比较器
     * @param nullMin    null视为最小
     * @return 最值结果
     */
    public static <T> CollectorImpl<T, Peak<T>, Peak<Any<T>>> peak(Comparator<? super T> comparator, boolean nullMin) {
        Sorter<T> sorter = new Sorter<>(nullMin);
        sorter.asc(comparator, nullMin);
        return buildPeak(sorter.getComparator());
    }

    /**
     * 最值收集器
     *
     * @param fun     属性
     * @param nullMin null视为最小
     * @return 最值结果
     */
    public static <T, N extends Comparable<? super N>> CollectorImpl<T, Peak<T>, Peak<Any<T>>> peak(Function1<? super T, ? extends N> fun, boolean nullMin) {
        Sorter<T> sorter = new Sorter<>(nullMin);
        sorter.asc(fun, nullMin);
        return buildPeak(sorter.getComparator());
    }

    private static <T> CollectorImpl<T, Peak<T>, Peak<Any<T>>> buildPeak(Comparator<? super T> finalComparator) {
        return new CollectorImpl<>(
                Peak::new,
                (p, a) -> {
                    T max = p.getMax();
                    T min = p.getMin();
                    if (Objects.isNull(max) || finalComparator.compare(max, a) < 0) {
                        p.setMax(a);
                    }
                    if (Objects.isNull(max) || finalComparator.compare(min, a) > 0) {
                        p.setMin(a);
                    }
                },
                (p1, p2) -> {
                    T max1 = p1.getMax();
                    T min1 = p1.getMin();
                    T max2 = p2.getMax();
                    T min2 = p2.getMin();
                    if (finalComparator.compare(max1, max2) < 0) {
                        p1.setMax(max2);
                    }
                    if (finalComparator.compare(min1, min2) > 0) {
                        p1.setMin(min2);
                    }
                    return p1;
                },
                p -> new Peak<>(Any.of(p.getMax()), Any.of(p.getMin())),
                Collections.emptySet()
        );
    }

    /**
     * join
     *
     * @param delimiter 分隔符
     * @return {@link Collector}<{@link T}, {@link ?}, {@link String}>
     */
    public static <T> Collector<T, ?, String> join(CharSequence delimiter) {
        return join(delimiter, "", "");
    }

    /**
     * join
     *
     * @param delimiter 分隔符
     * @param prefix    前缀
     * @param suffix    后缀
     * @return {@link Collector}<{@link T}, {@link ?}, {@link String}>
     */
    public static <T> Collector<T, ?, String> join(CharSequence delimiter,
                                                   CharSequence prefix,
                                                   CharSequence suffix) {
        return new CollectorImpl<>(
                () -> new StringJoiner(delimiter, prefix, suffix),
                (s, t) -> {
                    if (Objects.nonNull(t)) {
                        s.add(t.toString());
                    } else {
                        s.add(null);
                    }
                },
                StringJoiner::merge,
                StringJoiner::toString,
                Collections.emptySet()
        );
    }

    /**
     * 分组
     * 标准流的分组不允许key为null，故改
     *
     * @return {@link Collector}<{@link T}, {@link ?}, {@link M}>
     * @see ExCollectors#groupingBy(Function, Supplier, Collector)
     */
    public static <T, K, D, A, M extends Map<K, D>>
    Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier,
                                  Supplier<M> mapFactory,
                                  Collector<? super T, A, D> downstream) {
        Supplier<A> downstreamSupplier = downstream.supplier();
        BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
        BiConsumer<Map<K, A>, T> accumulator = (m, t) -> {
            K key = null;
            if (Objects.nonNull(t)) {
                key = classifier.apply(t);
            }
            A container = m.computeIfAbsent(key, k -> downstreamSupplier.get());
            downstreamAccumulator.accept(container, t);
        };
        BinaryOperator<Map<K, A>> merger = ExCollectors.mapMerger(downstream.combiner());
        @SuppressWarnings("unchecked")
        Supplier<Map<K, A>> mangledFactory = (Supplier<Map<K, A>>) mapFactory;

        if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl<>(mangledFactory, accumulator, merger, Collections.emptySet());
        } else {
            @SuppressWarnings("unchecked")
            Function<A, A> downstreamFinisher = (Function<A, A>) downstream.finisher();
            Function<Map<K, A>, M> finisher = intermediate -> {
                intermediate.replaceAll((k, v) -> downstreamFinisher.apply(v));
                @SuppressWarnings("unchecked")
                M castResult = (M) intermediate;
                return castResult;
            };
            return new CollectorImpl<>(mangledFactory, accumulator, merger, finisher, Collections.emptySet());
        }
    }

    public static <T, U, A, R>
    Collector<T, ?, R> mapping(Function<? super T, ? extends U> mapper,
                               Collector<? super U, A, R> downstream) {
        BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
        return new CollectorImpl<>(downstream.supplier(),
                (r, t) -> {
                    U value = null;
                    if (Objects.nonNull(t)) {
                        value = mapper.apply(t);
                    }
                    downstreamAccumulator.accept(r, value);
                },
                downstream.combiner(), downstream.finisher(),
                downstream.characteristics());
    }

    /**
     * 合并
     *
     * @param mergeFunction 合并功能
     * @return {@link BinaryOperator}<{@link M}>
     */
    private static <K, V, M extends Map<K, V>>
    BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet()) {
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            }
            return m1;
        };
    }

    public static <T> Collector<T, ?, Any<T>> last() {
        return Collector.of(() -> new Box<T>(null),
                Box::setA,
                (box1, box2) -> Objects.equals(box2.getA(), null) ? box1 : box2,
                box -> Any.of(box.getA()));
    }
}

package com.zhu56.stream;


import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 自定义收集器
 *
 * @author 朱滔
 * @date 2023/04/23 21:58:06
 */
class MyCollectors {


    /**
     * 收集器实现
     * Collectors.CollectorImpl 不给我用我不会抄一个过来吗
     */
    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
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

    static class AvgCollector<T, N extends Number> implements Collector<T, BigDecimal, N> {

        private final Function<T, N> fun;

        private final Integer count = 0;

        AvgCollector(Function<T, N> fun) {
            this.fun = fun;
        }


        @Override
        public BiConsumer<BigDecimal, T> accumulator() {
            return (sum, t) -> sum.add(NumberUtil.toBigDecimal(fun.apply(t)));
        }

        @Override
        public Supplier<BigDecimal> supplier() {
            return null;
        }

        @Override
        public BinaryOperator<BigDecimal> combiner() {
            return null;
        }

        @Override
        public Function<BigDecimal, N> finisher() {
            return null;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return null;
        }
    }

    /**
     * 按属性去重
     *
     * @param fun      属性
     * @param override 去重
     * @return {@link CollectorImpl}<{@link T}, {@link Map}<{@link Object}, {@link T}>, {@link Ztream}<{@link T}>>
     */
    public static <T> CollectorImpl<T, Map<Object, T>, Ztream<T>> distinct(Function<? super T, ?> fun, boolean override) {
        return new CollectorImpl<>(
                LinkedHashMap::new,
                (map, o) -> {
                    BiConsumer<Object, T> consumer = override ? map::put : map::putIfAbsent;
                    consumer.accept(fun.apply(o), o);
                },
                (m1, m2) -> {
                    BiConsumer<Object, T> consumer = override ? m1::put : m1::putIfAbsent;
                    m2.values().forEach(o -> consumer.accept(fun.apply(o), o));
                    return m1;
                },
                map -> Ztream.of(map.values()),
                Collections.emptySet()
        );
    }


    public static <T, N extends Number> CollectorImpl<T, Map<Object, T>, Ztream<T>> avg(Function<? super T, N> fun) {
        return null;
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
     * @see Collectors#groupingBy(Function, Supplier, Collector)
     */
    public static <T, K, D, A, M extends Map<K, D>>
    Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier,
                                  Supplier<M> mapFactory,
                                  Collector<? super T, A, D> downstream) {
        Supplier<A> downstreamSupplier = downstream.supplier();
        BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
        BiConsumer<Map<K, A>, T> accumulator = (m, t) -> {
            K key = classifier.apply(t);
            A container = m.computeIfAbsent(key, k -> downstreamSupplier.get());
            downstreamAccumulator.accept(container, t);
        };
        BinaryOperator<Map<K, A>> merger = MyCollectors.mapMerger(downstream.combiner());
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
}

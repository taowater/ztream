package com.taowater.ztream;


import org.dromara.hutool.core.util.RandomUtil;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * 抽象增强流
 *
 * @author Zhu56
 * @date 2022/11/13 21:37:12
 */
@SuppressWarnings("unchecked")
abstract class AbstractZtream<T, S extends Stream<T>> implements Stream<T>, Iterable<T> {

    protected final Stream<T> stream;

    protected AbstractZtream(Stream<T> stream) {
        this.stream = stream;
    }

    /**
     * 将标准流包装为增强流
     *
     * @param stream 流
     * @return 包装的结果
     */
    protected abstract S wrap(Stream<T> stream);

    @Override
    public S filter(Predicate<? super T> predicate) {
        return wrap(stream.filter(predicate));
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super T> mapper) {
        return stream.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super T> mapper) {
        return stream.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return stream.mapToDouble(mapper);
    }


    @Override
    public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return stream.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return stream.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return stream.flatMapToDouble(mapper);
    }

    @Override
    public S distinct() {
        return wrap(stream.distinct());
    }

    @Override
    public S sorted() {
        return wrap(stream.sorted());
    }

    @Override
    public S sorted(Comparator<? super T> comparator) {
        return wrap(stream.sorted(comparator));
    }

    @Override
    @SuppressWarnings("all")
    public S peek(Consumer<? super T> action) {
        return wrap(stream.peek(action));
    }

    @Override
    public S limit(long maxSize) {
        return wrap(stream.limit(maxSize));
    }

    @Override
    public S skip(long n) {
        return wrap(stream.skip(n));
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        stream.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
        stream.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return stream.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return stream.toArray(generator);
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return stream.reduce(identity, accumulator);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return stream.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        return stream.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        return stream.collect(collector);
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        return stream.min(comparator);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        return stream.max(comparator);
    }

    @Override
    public long count() {
        return stream.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return stream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return stream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return stream.noneMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() {
        return stream.findFirst();
    }

    @Override
    public Optional<T> findAny() {
        return stream.findAny();
    }

    @Override
    public Iterator<T> iterator() {
        return stream.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return stream.isParallel();
    }

    @Override
    public S sequential() {
        return wrap(stream.sequential());
    }

    @Override
    public S parallel() {
        return wrap(stream.parallel());
    }

    @Override
    public S unordered() {
        return wrap(stream.unordered());
    }

    @Override
    public S onClose(Runnable closeHandler) {
        return wrap(stream.onClose(closeHandler));
    }

    @Override
    public void close() {
        stream.close();
    }

    public S distinct(Function<? super T, ?> fun) {
        return wrap(map(t -> {
            Object v = null;
            if (Objects.nonNull(t)) {
                v = fun.apply(t);
            }
            return new PairBox<>(t, v);
        }).distinct().map(box -> box.a));
    }

    /**
     * 第一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> first() {
        return Any.of(findFirst().orElse(null));
    }

    /**
     * 任意一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> any() {
        return Any.of(findAny().orElse(null));
    }

    /**
     * 对元素进行洗牌
     *
     * @return {@link Ztream}<{@link T}>
     */
    public S shuffle() {
        return wrap(map(e -> new PairBox<>(e, RandomUtil.randomInt())).sorted(Comparator.comparing(e -> e.b)).map(e -> e.a));
    }

    /**
     * 反转顺序
     *
     * @return {@link S }
     */
    public S reverse() {
        return sorted((Comparator<? super T>) Comparator.reverseOrder());
    }

    /**
     * 随机取一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> random() {
        return Any.of(shuffle().findFirst().orElse(null));
    }

    /**
     * 过滤元素非空
     *
     * @return {@link Ztream}<{@link T}>
     */
    public S nonNull() {
        return this.filter(Objects::nonNull);
    }

    /**
     * 判断元素是否有重复
     *
     * @return boolean
     */
    public boolean hadRepeat() {
        Set<T> set = new HashSet<>();
        return anyMatch(x -> !set.add(x));
    }

    /**
     * 升序
     *
     * @param fun 属性
     * @return {@link Ztream}<{@link T}>
     */
    public <U extends Comparable<? super U>> S asc(Function<? super T, ? extends U> fun) {
        return asc(fun, true);
    }

    /**
     * 升序
     *
     * @param fun       属性
     * @param nullFirst 是否null值前置
     * @return {@link Ztream }<{@link T }>
     */
    public <U extends Comparable<? super U>> S asc(Function<? super T, ? extends U> fun, boolean nullFirst) {
        return sort(r -> r.asc(fun, nullFirst));
    }

    /**
     * 升序
     *
     * @return {@link Ztream }<{@link T }>
     */
    public S asc() {
        return asc(true);
    }

    /**
     * 升序
     *
     * @param nullFirst 是否null值前置
     * @return {@link Ztream }<{@link T }>
     */
    public S asc(boolean nullFirst) {
        return sort(r -> r.then((Comparator<T>) Comparator.naturalOrder(), nullFirst));
    }

    /**
     * 降序
     *
     * @return {@link Ztream}<{@link T}>
     */
    public <U extends Comparable<? super U>> S desc(Function<? super T, ? extends U> fun) {
        return desc(fun, true);
    }

    /**
     * 降序
     *
     * @param fun       属性
     * @param nullFirst 是否null值前置
     * @return {@link Ztream }<{@link T }>
     */
    public <U extends Comparable<? super U>> S desc(Function<? super T, ? extends U> fun, boolean nullFirst) {
        return sort(r -> r.desc(fun, nullFirst));
    }

    /**
     * 降序
     *
     * @return {@link Ztream}<{@link T}>
     */
    public S desc() {
        return desc(true);
    }

    /**
     * 降序
     *
     * @param nullFirst 是否null值前置
     * @return {@link Ztream }<{@link T }>
     */
    public S desc(boolean nullFirst) {
        return sort(r -> r.then((Comparator<T>) Comparator.reverseOrder(), nullFirst));
    }

    /**
     * 排序
     *
     * @param consumer 排序上下分的消费函数
     * @return {@link Ztream}<{@link T}>
     */
    public S sort(Consumer<Sorter<T>> consumer) {
        Sorter<T> sorter = new Sorter<>();
        consumer.accept(sorter);
        return sorted(sorter.getComparator());
    }


    static class Box<A> implements Consumer<A> {
        A a;

        Box() {
        }

        Box(A obj) {
            this.a = obj;
        }

        @Override
        public void accept(A a) {
            this.a = a;
        }
    }

    static class PairBox<A, B> extends Box<A> {
        B b;

        PairBox(A a, B b) {
            super(a);
            this.b = b;
        }

        static <T> PairBox<T, T> single(T a) {
            return new PairBox<>(a, a);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(b);
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == PairBox.class && Objects.equals(b, ((PairBox<?, ?>) obj).b);
        }
    }
}

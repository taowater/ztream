package com.taowater.ztream;


import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.*;

/**
 * ztream 接口
 *
 * @author Zhu56
 * @date 2022/11/13 21:37:12
 */
interface IZtream<T, S extends Stream<T>> extends Stream<T>, Iterable<T> {

    /**
     * 获取被当前实例包装的流对象
     *
     * @return 被当前实例包装的流对象
     */
    Stream<T> unwrap();


    /**
     * 将标准流包装为增强流
     *
     * @param stream 流
     * @return 包装的结果
     */
    S wrap(Stream<T> stream);

    /**
     * 根据flag增加一个中间操作
     *
     * @param flag 标识
     * @param fun  函数
     * @return {@link S }
     */
    default S handle(boolean flag, Function<? super S, S> fun) {
        if (flag) {
            return fun.apply(wrap(this));
        }
        return wrap(this);
    }

    @Override
    default S filter(Predicate<? super T> predicate) {
        return wrap(unwrap().filter(predicate));
    }

    @Override
    default IntStream mapToInt(ToIntFunction<? super T> mapper) {
        return unwrap().mapToInt(mapper);
    }

    @Override
    default LongStream mapToLong(ToLongFunction<? super T> mapper) {
        return unwrap().mapToLong(mapper);
    }

    @Override
    default DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return unwrap().mapToDouble(mapper);
    }

    @Override
    default IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return unwrap().flatMapToInt(mapper);
    }

    @Override
    default LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return unwrap().flatMapToLong(mapper);
    }

    @Override
    default DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return unwrap().flatMapToDouble(mapper);
    }

    @Override
    default S distinct() {
        return wrap(unwrap().distinct());
    }

    @Override
    default S sorted() {
        return wrap(unwrap().sorted());
    }

    @Override
    default S sorted(Comparator<? super T> comparator) {
        return wrap(unwrap().sorted(comparator));
    }

    @Override
    @SuppressWarnings("all")
    default S peek(Consumer<? super T> action) {
        return wrap(unwrap().peek(action));
    }

    @Override
    default S limit(long maxSize) {
        return wrap(unwrap().limit(maxSize));
    }

    @Override
    default S skip(long n) {
        return wrap(unwrap().skip(n));
    }

    @Override
    default void forEach(Consumer<? super T> action) {
        unwrap().forEach(action);
    }

    @Override
    default void forEachOrdered(Consumer<? super T> action) {
        unwrap().forEachOrdered(action);
    }

    @Override
    default Object[] toArray() {
        return unwrap().toArray();
    }

    @Override
    default <A> A[] toArray(IntFunction<A[]> generator) {
        return unwrap().toArray(generator);
    }

    @Override
    default T reduce(T identity, BinaryOperator<T> accumulator) {
        return unwrap().reduce(identity, accumulator);
    }

    @Override
    default Optional<T> reduce(BinaryOperator<T> accumulator) {
        return unwrap().reduce(accumulator);
    }

    @Override
    default <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        return unwrap().reduce(identity, accumulator, combiner);
    }

    @Override
    default <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return unwrap().collect(supplier, accumulator, combiner);
    }

    @Override
    default <R, A> R collect(Collector<? super T, A, R> collector) {
        return unwrap().collect(collector);
    }

    @Override
    default Optional<T> min(Comparator<? super T> comparator) {
        return unwrap().min(comparator);
    }

    @Override
    default Optional<T> max(Comparator<? super T> comparator) {
        return unwrap().max(comparator);
    }

    @Override
    default long count() {
        return unwrap().count();
    }

    @Override
    default boolean anyMatch(Predicate<? super T> predicate) {
        return unwrap().anyMatch(predicate);
    }

    @Override
    default boolean allMatch(Predicate<? super T> predicate) {
        return unwrap().allMatch(predicate);
    }

    @Override
    default boolean noneMatch(Predicate<? super T> predicate) {
        return unwrap().noneMatch(predicate);
    }

    @Override
    default Optional<T> findFirst() {
        return unwrap().findFirst();
    }

    @Override
    default Optional<T> findAny() {
        return unwrap().findAny();
    }

    @Override
    default Iterator<T> iterator() {
        return unwrap().iterator();
    }

    @Override
    default Spliterator<T> spliterator() {
        return unwrap().spliterator();
    }

    @Override
    default boolean isParallel() {
        return unwrap().isParallel();
    }

    @Override
    default S sequential() {
        return wrap(unwrap().sequential());
    }

    @Override
    default S parallel() {
        return wrap(unwrap().parallel());
    }

    @Override
    default S unordered() {
        return wrap(unwrap().unordered());
    }

    @Override
    default S onClose(Runnable closeHandler) {
        return wrap(unwrap().onClose(closeHandler));
    }

    @Override
    default void close() {
        unwrap().close();
    }
}

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
public interface IZtream<T, S extends Stream<T>> extends Stream<T>, Iterable<T> {

    /**
     * 获取原始流
     */
    Stream<T> stream();


    /**
     * 将标准流包装为增强流
     *
     * @param stream 流
     * @return 包装的结果
     */
    S ztream(Stream<T> stream);

    @Override
    default S filter(Predicate<? super T> predicate) {
        return ztream(stream().filter(predicate));
    }

    @Override
    default IntStream mapToInt(ToIntFunction<? super T> mapper) {
        return stream().mapToInt(mapper);
    }

    @Override
    default LongStream mapToLong(ToLongFunction<? super T> mapper) {
        return stream().mapToLong(mapper);
    }

    @Override
    default DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return stream().mapToDouble(mapper);
    }

    @Override
    default IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return stream().flatMapToInt(mapper);
    }

    @Override
    default LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return stream().flatMapToLong(mapper);
    }

    @Override
    default DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return stream().flatMapToDouble(mapper);
    }

    @Override
    default S distinct() {
        return ztream(stream().distinct());
    }

    @Override
    default S sorted() {
        return ztream(stream().sorted());
    }

    @Override
    default S sorted(Comparator<? super T> comparator) {
        return ztream(stream().sorted(comparator));
    }

    @Override
    @SuppressWarnings("all")
    default S peek(Consumer<? super T> action) {
        return ztream(stream().peek(action));
    }

    @Override
    default S limit(long maxSize) {
        return ztream(stream().limit(maxSize));
    }

    @Override
    default S skip(long n) {
        return ztream(stream().skip(n));
    }

    @Override
    default void forEach(Consumer<? super T> action) {
        stream().forEach(action);
    }

    @Override
    default void forEachOrdered(Consumer<? super T> action) {
        stream().forEachOrdered(action);
    }

    @Override
    default Object[] toArray() {
        return stream().toArray();
    }

    @Override
    default <A> A[] toArray(IntFunction<A[]> generator) {
        return stream().toArray(generator);
    }

    @Override
    default T reduce(T identity, BinaryOperator<T> accumulator) {
        return stream().reduce(identity, accumulator);
    }

    @Override
    default Optional<T> reduce(BinaryOperator<T> accumulator) {
        return stream().reduce(accumulator);
    }

    @Override
    default <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        return stream().reduce(identity, accumulator, combiner);
    }

    @Override
    default <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return stream().collect(supplier, accumulator, combiner);
    }

    @Override
    default <R, A> R collect(Collector<? super T, A, R> collector) {
        return stream().collect(collector);
    }

    @Override
    default Optional<T> min(Comparator<? super T> comparator) {
        return stream().min(comparator);
    }

    @Override
    default Optional<T> max(Comparator<? super T> comparator) {
        return stream().max(comparator);
    }

    @Override
    default long count() {
        return stream().count();
    }

    @Override
    default boolean anyMatch(Predicate<? super T> predicate) {
        return stream().anyMatch(predicate);
    }

    @Override
    default boolean allMatch(Predicate<? super T> predicate) {
        return stream().allMatch(predicate);
    }

    @Override
    default boolean noneMatch(Predicate<? super T> predicate) {
        return stream().noneMatch(predicate);
    }

    @Override
    default Optional<T> findFirst() {
        return stream().findFirst();
    }

    /**
     * 首个元素
     *
     * @param throwNpe 是否抛出空指针异常
     * @return 结果
     */
    default Optional<T> findFirst(boolean throwNpe) {
        try {
            return stream().findFirst();
        } catch (NullPointerException e) {
            if (throwNpe) {
                throw e;
            }
            return Optional.empty();
        }
    }

    @Override
    default Optional<T> findAny() {
        return stream().findAny();
    }

    /**
     * 任意一个
     *
     * @param throwNpe 是否抛出空指针异常
     * @return 结果
     */
    default Optional<T> findAny(boolean throwNpe) {
        try {
            return stream().findAny();
        } catch (NullPointerException e) {
            if (throwNpe) {
                throw e;
            }
            return Optional.empty();
        }
    }

    @Override
    default Iterator<T> iterator() {
        return stream().iterator();
    }

    @Override
    default Spliterator<T> spliterator() {
        return stream().spliterator();
    }

    @Override
    default boolean isParallel() {
        return stream().isParallel();
    }

    @Override
    default S sequential() {
        return ztream(stream().sequential());
    }

    @Override
    default S parallel() {
        return ztream(stream().parallel());
    }

    @Override
    default S unordered() {
        return ztream(stream().unordered());
    }

    @Override
    default S onClose(Runnable closeHandler) {
        return ztream(stream().onClose(closeHandler));
    }

    @Override
    default void close() {
        stream().close();
    }
}

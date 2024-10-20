package com.taowater.ztream.assist;

import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * 排序器
 *
 * @author zhu56
 * @date 2024/05/22 00:56
 */
@NoArgsConstructor
public class Sorter<T> {

    /**
     * 默认排序不改变顺序
     */
    private Comparator<T> comparator = (o1, o2) -> 0;

    /**
     * 元素为null时是否前置
     */
    private boolean nullFirst = true;

    public Sorter(boolean nullFirst) {
        this.nullFirst = nullFirst;
    }

    @SuppressWarnings("unchecked")
    public Comparator<T> getComparator() {
        return (Comparator<T>) Sorter.<T>nullOrder(nullFirst).apply(comparator);
    }

    /**
     * 空值排序处理
     *
     * @param nullFirst 是否null值前置
     * @return {@link UnaryOperator }<{@link Comparator }<{@link T }>>
     */
    private static <T> UnaryOperator<Comparator<? super T>> nullOrder(boolean nullFirst) {
        return nullFirst ? Comparator::nullsFirst : Comparator::nullsLast;
    }

    /**
     * 排序
     *
     * @param keyExtractor 属性
     * @param desc         是否倒序
     * @param nullFirst    是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> sort(Function<? super T, ? extends U> keyExtractor, boolean desc, boolean nullFirst) {
        comparator = comparator.thenComparing(build(keyExtractor, desc, nullFirst));
        return this;
    }

    /**
     * 自定义排序逻辑排序
     *
     * @param otherComparator 其他比较器
     * @param desc            是否倒序
     * @param nullFirst       是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public Sorter<T> sort(Comparator<T> otherComparator, boolean desc, boolean nullFirst) {
        comparator = comparator.thenComparing(build(otherComparator, desc, nullFirst));
        return this;
    }

    /**
     * null前置
     *
     * @return {@link Sorter }<{@link T }>
     */
    public Sorter<T> nullFirst() {
        this.nullFirst = true;
        return this;
    }

    /**
     * null后置
     *
     * @return {@link Sorter }<{@link T }>
     */
    public Sorter<T> nullLast() {
        this.nullFirst = false;
        return this;
    }

    /**
     * 升序
     *
     * @param keyExtractor 属性
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> asc(Function<? super T, ? extends U> keyExtractor) {
        return sort(keyExtractor, false, true);
    }

    /**
     * 升序
     *
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> asc(Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return sort(keyExtractor, false, nullFirst);
    }

    /**
     * 自定义排序逻辑升序
     *
     * @param comparator 比较器
     * @param nullFirst  是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public Sorter<T> asc(Comparator<? super T> comparator, boolean nullFirst) {
        sort(comparator::compare, false, nullFirst);
        return this;
    }

    /**
     * 降序
     *
     * @param keyExtractor 属性
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> desc(Function<? super T, ? extends U> keyExtractor) {
        return sort(keyExtractor, true, true);
    }

    /**
     * 降序
     *
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> desc(Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return sort(keyExtractor, true, nullFirst);
    }

    /**
     * 自定义排序逻辑降序
     *
     * @param comparator 比较器
     * @param nullFirst  是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public Sorter<T> desc(Comparator<? super T> comparator, boolean nullFirst) {
        sort(comparator::compare, true, nullFirst);
        return this;
    }

    /**
     * 追加排序
     *
     * @param keyExtractor  属性
     * @param keyComparator 排序器
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> then(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return then(Comparator.comparing(keyExtractor, keyComparator), true);
    }

    /**
     * 追加排序
     *
     * @param otherComparator 其他比较器
     * @param nullFirst       null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public Sorter<T> then(Comparator<? super T> otherComparator, boolean nullFirst) {
        comparator = comparator.thenComparing(Sorter.<T>nullOrder(nullFirst).apply(otherComparator));
        return this;
    }

    /**
     * 排序
     *
     * @param keyExtractor  属性
     * @param keyComparator 比较器
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> sort(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        comparator = comparator.thenComparing(keyExtractor, keyComparator);
        return this;
    }

    /**
     * 根据属性构建排序器
     *
     * @param keyExtractor 属性
     * @param desc         是否反序
     * @param nullFirst    是否null值前置
     * @return {@link Comparator }<{@link T }>
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> build(Function<? super T, ? extends U> keyExtractor, boolean desc, boolean nullFirst) {
        Comparator<U> baseOrder = desc ? Comparator.reverseOrder() : Comparator.naturalOrder();
        return Comparator.comparing(keyExtractor, Sorter.<U>nullOrder(nullFirst).apply(baseOrder));
    }

    /**
     * 根据排序逻辑确定最终的排序器
     *
     * @param comparator 比较仪
     * @param desc       描述
     * @param nullFirst  是否null值前置
     * @return {@link Comparator }<{@link T }>
     */
    public static <T> Comparator<T> build(Comparator<? super T> comparator, boolean desc, boolean nullFirst) {
        Comparator<? super T> baseOrder = desc ? comparator.reversed() : comparator;
        return (Comparator<T>) Sorter.<T>nullOrder(nullFirst).apply(baseOrder);
    }
}

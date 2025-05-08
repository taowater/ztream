package com.taowater.ztream.op.sort;

import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * 排序器
 *
 * @author zhu56
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@NoArgsConstructor
public class Sorter<T> implements Asc<T, Sorter<T>>, Desc<T, Sorter<T>> {

    /**
     * 初始化排序为空
     */
    private Comparator<T> comparator = null;

    /**
     * 元素为null时是否前置
     */
    private boolean nullFirst = true;

    public Sorter(boolean nullFirst) {
        this.nullFirst = nullFirst;
    }

    public Comparator<T> getComparator() {
        return Sorter.<T>nullOrder(nullFirst).apply(comparator);
    }

    /**
     * 空值排序处理
     *
     * @param nullFirst 是否null值前置
     */
    private static <T> Function<Comparator<? super T>, Comparator<T>> nullOrder(boolean nullFirst) {
        return nullFirst ? Comparator::nullsFirst : Comparator::nullsLast;
    }

    /**
     * 排序
     *
     * @param keyExtractor 属性
     * @param desc         是否倒序
     * @param nullFirst    是否null值前置
     * @param condition    条件
     */
    public <U extends Comparable<? super U>> Sorter<T> sort(boolean condition, Function<? super T, ? extends U> keyExtractor, boolean desc, boolean nullFirst) {
        if (condition) {
            Comparator<U> baseOrder = desc ? Comparator.reverseOrder() : Comparator.naturalOrder();
            Comparator<T> otherComparator = Comparator.comparing(keyExtractor, Sorter.<U>nullOrder(nullFirst).apply(baseOrder));
            return then(otherComparator);
        }
        return this;
    }

    /**
     * 自定义排序逻辑排序
     *
     * @param otherComparator 其他比较器
     * @param desc            是否倒序
     * @param nullFirst       是否null值前置
     * @param condition       条件
     */
    public Sorter<T> sort(boolean condition, Comparator<T> otherComparator, boolean desc, boolean nullFirst) {
        if (condition) {
            Comparator<? super T> baseOrder = desc ? otherComparator.reversed() : otherComparator;
            Comparator<T> realOtherComparator = Sorter.<T>nullOrder(nullFirst).apply(baseOrder);
            return then(realOtherComparator);
        }
        return this;
    }

    /**
     * 排序
     *
     * @param keyExtractor 属性
     * @param desc         是否倒序
     * @param nullFirst    是否null值前置
     */
    public <U extends Comparable<? super U>> Sorter<T> sort(Function<? super T, ? extends U> keyExtractor, boolean desc, boolean nullFirst) {
        return sort(true, keyExtractor, desc, nullFirst);
    }


    /**
     * 设置null前置
     */
    public Sorter<T> nullFirst(boolean nullFirst) {
        this.nullFirst = nullFirst;
        return this;
    }

    /**
     * 追加排序
     *
     * @param keyExtractor  属性
     * @param keyComparator 排序器
     */
    public <U extends Comparable<? super U>> Sorter<T> then(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return then(Comparator.comparing(keyExtractor, keyComparator), true);
    }

    /**
     * 追加排序
     *
     * @param otherComparator 其他比较器
     * @param nullFirst       null值前置
     */
    public Sorter<T> then(Comparator<? super T> otherComparator, boolean nullFirst) {
        Comparator<T> realOtherComparator = Sorter.<T>nullOrder(nullFirst).apply(otherComparator);
        return then(realOtherComparator);
    }

    /**
     * 追加排序器
     *
     * @param otherComparator 其他比较器
     */
    public Sorter<T> then(Comparator<T> otherComparator) {
        if (Objects.isNull(comparator)) {
            comparator = otherComparator;
        } else {
            comparator = comparator.thenComparing(otherComparator);
        }
        return this;
    }

    /**
     * 排序
     *
     * @param keyExtractor  属性
     * @param keyComparator 比较器
     */
    public <U extends Comparable<? super U>> Sorter<T> sort(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return then(Comparator.comparing(keyExtractor, keyComparator));
    }

    @Override
    public <U extends Comparable<? super U>> Sorter<T> desc(boolean condition, Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return sort(condition, keyExtractor, true, nullFirst);
    }

    @Override
    public Sorter<T> desc(boolean condition, Comparator<? super T> comparator, boolean nullFirst) {
        return sort(condition, comparator::compare, true, nullFirst);
    }

    @Override
    public <U extends Comparable<? super U>> Sorter<T> asc(boolean condition, Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return sort(condition, keyExtractor, false, nullFirst);
    }

    @Override
    public Sorter<T> asc(boolean condition, Comparator<? super T> comparator, boolean nullFirst) {
        return sort(condition, comparator::compare, false, nullFirst);
    }
}

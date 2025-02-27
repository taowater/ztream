package com.taowater.ztream.op.math;


import com.taowater.ztream.Any;
import com.taowater.ztream.IZtream;
import com.taowater.ztream.assist.ExCollectors;
import io.vavr.Function1;
import lombok.var;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 数学统计相关
 *
 * @author zhu56
 * @since 0.0.1
 */
public interface Math<T, S extends IZtream<T, S>> extends IZtream<T, S> {

    /**
     * 累加
     *
     * @param fun 属性
     * @return 和
     */
    default <N extends Number> N sum(Function1<? super T, ? extends N> fun) {
        return sum(fun, null);
    }

    /**
     * 累加
     *
     * @param fun          属性方法引用
     * @param defaultValue 默认值
     * @return 和
     */
    default <N extends Number> N sum(Function1<? super T, ? extends N> fun, N defaultValue) {
        var result = filter(Objects::nonNull)
                .map(fun)
                .map(BigDecimalStrategy::toBigDecimal)
                .reduce((a, b) -> Any.of(a).map(e -> e.add(Any.of(b).orElse(BigDecimal.ZERO))).orElse(BigDecimal.ZERO))
                .map(b -> BigDecimalStrategy.getValue(b, fun));
        if (result.isPresent()) {
            return result.get();
        }
        return defaultValue;
    }

    /**
     * 按属性取最小的元素
     *
     * @param fun     属性
     * @param nullMin null视为最小
     * @return {@link T }
     */
    default <V extends Comparable<? super V>> Any<T> minBy(Function1<? super T, ? extends V> fun, boolean nullMin) {
        return this.collect(ExCollectors.peak(fun, nullMin)).getMin();
    }

    /**
     * 按属性取最小的元素
     *
     * @param fun 属性
     * @return {@link T }
     */
    default <V extends Comparable<? super V>> Any<T> minBy(Function1<? super T, ? extends V> fun) {
        return this.minBy(fun, true);
    }

    /**
     * 按属性取最大的元素
     *
     * @param fun     属性
     * @param nullMin null视为最小
     * @return {@link T }
     */
    default <V extends Comparable<? super V>> Any<T> maxBy(Function1<? super T, ? extends V> fun, boolean nullMin) {
        return this.collect(ExCollectors.peak(fun, nullMin)).getMax();
    }

    /**
     * 按属性取最大的元素
     *
     * @param fun 属性
     * @return {@link T }
     */
    default <V extends Comparable<? super V>> Any<T> maxBy(Function1<? super T, ? extends V> fun) {
        return this.maxBy(fun, true);
    }

    /**
     * 最大
     *
     * @return 结果
     */
    default Any<T> max() {
        return this.max(true);
    }

    /**
     * 最大
     *
     * @param nullMin null视为最小
     * @return 结果
     */
    @SuppressWarnings("unchecked")
    default Any<T> max(boolean nullMin) {
        return this.collect(ExCollectors.peak(
                (a, b) -> {
                    if (a instanceof Comparable) {
                        return ((Comparable<T>) a).compareTo(b);
                    }
                    return a.toString().compareTo(b.toString());
                }, nullMin
        )).getMax();
    }

    /**
     * 最大值
     *
     * @param fun 函数
     * @return 最大值
     */
    default <N extends Comparable<N>> N max(Function1<? super T, ? extends N> fun) {
        return max(fun, null);
    }

    /**
     * 最大值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return 最大值
     */
    default <N extends Comparable<N>> N max(Function1<? super T, ? extends N> fun, N defaultValue) {
        return this.maxBy(fun).get(fun, defaultValue);
    }

    /**
     * 最小值
     *
     * @param fun 函数
     * @return 最小值
     */
    default <N extends Comparable<N>> N min(Function1<? super T, ? extends N> fun) {
        return min(fun, null);
    }

    /**
     * 最小
     *
     * @return 结果
     */
    default Any<T> min() {
        return this.min(true);
    }

    /**
     * 最小
     *
     * @param nullMin null视为最小
     * @return 结果
     */
    @SuppressWarnings("unchecked")
    default Any<T> min(boolean nullMin) {
        return this.collect(ExCollectors.peak(
                (a, b) -> {
                    if (a instanceof Comparable) {
                        return ((Comparable<T>) a).compareTo(b);
                    }
                    return a.toString().compareTo(b.toString());
                }, nullMin
        )).getMin();
    }

    /**
     * 最小值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return 最小值
     */
    default <N extends Comparable<N>> N min(Function1<? super T, ? extends N> fun, N defaultValue) {
        return this.minBy(fun).get(fun, defaultValue);
    }

    /**
     * 平均值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @param nullCount    null是否计数
     * @return 平均值
     */
    default <N extends Number> N avg(Function1<? super T, ? extends N> fun, N defaultValue, boolean nullCount) {
        var result = collect(ExCollectors.avg(fun, nullCount));
        return Any.of(result).orElse(defaultValue);
    }

    /**
     * 平均值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return 平均值
     */
    default <N extends Number> N avg(Function1<? super T, ? extends N> fun, N defaultValue) {
        return avg(fun, defaultValue, true);
    }
}

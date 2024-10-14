package com.taowater.ztream.op;


import com.taowater.ztream.Any;
import com.taowater.ztream.assist.BigDecimalStrategy;
import com.taowater.ztream.assist.ExCollectors;
import com.taowater.ztream.assist.Sorter;
import io.vavr.Function1;
import lombok.var;
import org.dromara.hutool.core.math.NumberUtil;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 数学统计相关
 *
 * @author zhu56
 * @since 0.0.1
 */
public interface Math<T> extends Stream<T> {

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
                .map(NumberUtil::toBigDecimal)
                .reduce(NumberUtil::add)
                .map(b -> BigDecimalStrategy.getValue(b, fun));
        if (result.isPresent()) {
            return result.get();
        }
        return defaultValue;
    }

    /**
     * 按属性取最小的元素
     *
     * @param fun       属性
     * @param nullFirst 是否null值前置
     * @return {@link T }
     */
    default <V extends Comparable<? super V>> Any<T> minBy(Function<? super T, ? extends V> fun, boolean nullFirst) {
        return this.min(Sorter.build(fun, false, nullFirst)).map(Any::of).orElse(Any.empty());
    }

    /**
     * 按属性取最大的元素
     *
     * @param fun       属性
     * @param nullFirst 是否null值前置
     * @return {@link T }
     */
    default <V extends Comparable<? super V>> Any<T> maxBy(Function<? super T, ? extends V> fun, boolean nullFirst) {
        return this.max(Sorter.build(fun, false, nullFirst)).map(Any::of).orElse(Any.empty());
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
        var result = filter(Objects::nonNull)
                .map(fun)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.compareTo(b) >= 0 ? a : b);
        if (result.isPresent()) {
            return result.get();
        }
        return defaultValue;
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
     * 最小值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return 最小值
     */
    default <N extends Comparable<N>> N min(Function1<? super T, ? extends N> fun, N defaultValue) {
        var result = filter(Objects::nonNull)
                .map(fun)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.compareTo(b) < 0 ? a : b);
        if (result.isPresent()) {
            return result.get();
        }
        return defaultValue;
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

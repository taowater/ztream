package io.github.taowater.ztream;


import io.github.taowater.inter.SerFunction;
import org.dromara.hutool.core.math.NumberUtil;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 数学统计相关
 *
 * @author zhu56
 * @version 1.0
 * @date 2022/11/14 9:53
 */
interface Math<T> extends Stream<T> {

    /**
     * 累加
     *
     * @param fun 属性
     * @return 统计值
     */
    default <N extends Number> N sum(SerFunction<T, N> fun) {
        return this.sum(fun, null);
    }

    /**
     * 累加
     *
     * @param fun          属性方法引用
     * @param defaultValue 默认值
     * @return 统计值
     */
    default <N extends Number> N sum(SerFunction<T, N> fun, N defaultValue) {
        return this
                .filter(Objects::nonNull)
                .map(fun)
                .filter(Objects::nonNull)
                .map(NumberUtil::toBigDecimal)
                .reduce(NumberUtil::add)
                .map(b -> BigDecimalStrategy.getValue(b, fun))
                .orElse(defaultValue);
    }

    /**
     * 最大值
     *
     * @param fun 函数
     * @return {@link N}
     */
    default <N extends Comparable<N>> N max(SerFunction<T, N> fun) {
        return this.max(fun, null);
    }

    /**
     * 最大值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return {@link N}
     */
    default <N extends Comparable<N>> N max(SerFunction<T, N> fun, N defaultValue) {
        return this
                .filter(Objects::nonNull)
                .map(fun)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.compareTo(b) >= 0 ? a : b)
                .orElse(defaultValue);
    }

    /**
     * 最小值
     *
     * @param fun 函数
     * @return {@link N}
     */
    default <N extends Comparable<N>> N min(SerFunction<T, N> fun) {
        return this.min(fun, null);
    }

    /**
     * 最小值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return {@link N}
     */
    default <N extends Comparable<N>> N min(SerFunction<T, N> fun, N defaultValue) {
        return this
                .filter(Objects::nonNull)
                .map(fun)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.compareTo(b) < 0 ? a : b)
                .orElse(defaultValue);
    }

    default <N extends Number> N avg(SerFunction<T, N> fun, N defaultValue) {
        return Any.of(this.collect(ExCollectors.avg(fun))).orElse(defaultValue);
    }
}

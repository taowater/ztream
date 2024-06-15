package io.github.taowater.ztream;


import io.github.taowater.core.function.SerFunction;
import lombok.var;
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
    default <N extends Number> N sum(SerFunction<? super T, ? extends N> fun) {
        return this.sum(fun, null);
    }

    /**
     * 累加
     *
     * @param fun          属性方法引用
     * @param defaultValue 默认值
     * @return 统计值
     */
    default <N extends Number> N sum(SerFunction<? super T, ? extends N> fun, N defaultValue) {
        var result = this
                .filter(Objects::nonNull)
                .map(fun)
                .filter(Objects::nonNull)
                .map(NumberUtil::toBigDecimal)
                .reduce(NumberUtil::add)
                .map(b -> BigDecimalStrategy.getValue(b, fun));
        if (result.isPresent()) {
            return result.get();
        }
        return defaultValue;
    }

    /**
     * 最大值
     *
     * @param fun 函数
     * @return {@link N}
     */
    default <N extends Comparable<N>> N max(SerFunction<? super T, ? extends N> fun) {
        return this.max(fun, null);
    }

    /**
     * 最大值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return {@link N}
     */
    default <N extends Comparable<N>> N max(SerFunction<? super T, ? extends N> fun, N defaultValue) {
        var result = this
                .filter(Objects::nonNull)
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
     * @return {@link N}
     */
    default <N extends Comparable<N>> N min(SerFunction<? super T, ? extends N> fun) {
        return this.min(fun, null);
    }

    /**
     * 最小值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return {@link N}
     */
    default <N extends Comparable<N>> N min(SerFunction<? super T, ? extends N> fun, N defaultValue) {
        var result = this
                .filter(Objects::nonNull)
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
     * @return {@link N }
     */
    default <N extends Number> N avg(SerFunction<? super T, ? extends N> fun, N defaultValue, boolean nullCount) {
        var result = this.collect(ExCollectors.avg(fun, nullCount));
        return Any.of(result).orElse(defaultValue);
    }

    /**
     * 平均值
     *
     * @param fun          属性
     * @param defaultValue 默认值
     * @return {@link N }
     */
    default <N extends Number> N avg(SerFunction<? super T, ? extends N> fun, N defaultValue) {
        return avg(fun, defaultValue, true);
    }
}

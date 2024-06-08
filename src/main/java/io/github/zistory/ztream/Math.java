package io.github.zistory.ztream;


import cn.hutool.core.util.NumberUtil;
import io.github.zistory.Ztream;
import io.github.zistory.inter.SerFunction;

import java.util.stream.Stream;

/**
 * 数学统计相关
 *
 * @author zhu56
 * @version 1.0
 * @date 2022/11/14 9:53
 */
public interface Math<T> extends Stream<T> {

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
        return Ztream.of(this)
                .nonNull()
                .map(fun)
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
        return Ztream.of(this)
                .nonNull()
                .map(fun)
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
        return Ztream.of(this)
                .nonNull()
                .map(fun)
                .reduce((a, b) -> a.compareTo(b) < 0 ? a : b)
                .orElse(defaultValue);
    }
}

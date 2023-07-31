package com.zhu56.stream;


import cn.hutool.core.util.NumberUtil;
import com.zhu56.inter.SerFunction;

import java.util.stream.Stream;

/**
 * 流的数字相关操作
 *
 * @author zhu56
 * @version 1.0
 * @date 2022/11/14 9:53
 */
public interface StreamNumber<T> extends Stream<T> {

    /**
     * 对一个流的元素的某个数字类型属性进行累加统计
     *
     * @param fun 属性方法引用
     * @return 统计值
     */
    default <N extends Number> N sum(SerFunction<T, N> fun) {
        return this
                .map(fun)
                .map(NumberUtil::toBigDecimal)
                .reduce(NumberUtil::add)
                .map(b -> BigDecimalStrategy.getValue(b, fun))
                .orElse(null);
    }

    /**
     * 最大值
     *
     * @param fun 函数
     * @return {@link N}
     */
    default <N extends Number> N max(SerFunction<T, N> fun) {
        return this.map(fun)
                .map(NumberUtil::toBigDecimal)
                .reduce((a, b) -> a.compareTo(b) >= 0 ? a : b)
                .map(b -> BigDecimalStrategy.getValue(b, fun))
                .orElse(null);
    }

    /**
     * 最小值
     *
     * @param fun 函数
     * @return {@link N}
     */
    default <N extends Number> N min(SerFunction<T, N> fun) {
        return this
                .map(fun)
                .map(NumberUtil::toBigDecimal)
                .reduce((a, b) -> a.compareTo(b) < 0 ? a : b)
                .map(b -> BigDecimalStrategy.getValue(b, fun))
                .orElse(null);
    }

    default <N extends Number> N avg(SerFunction<T, N> fun) {
        return this
                .map(fun)
                .map(NumberUtil::toBigDecimal)
                .reduce((a, b) -> a.compareTo(b) < 0 ? a : b)
                .map(b -> BigDecimalStrategy.getValue(b, fun))
                .orElse(null);
    }
}

package com.taowater.ztream.op.sort;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 降序排序方法
 *
 * @author zhu56
 * @date 2024/10/24 00:47
 */
public interface Desc<T, S> {


    /**
     * 倒序
     *
     * @param condition    装载条件
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     */
    <U extends Comparable<? super U>> S desc(boolean condition, Function<? super T, ? extends U> keyExtractor, boolean nullFirst);


    /**
     * 倒序
     *
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     */
    default <U extends Comparable<? super U>> S desc(Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return desc(true, keyExtractor, nullFirst);
    }

    /**
     * 倒序
     *
     * @param condition    装载条件
     * @param keyExtractor 属性
     */
    default <U extends Comparable<? super U>> S desc(boolean condition, Function<? super T, ? extends U> keyExtractor) {
        return desc(condition, keyExtractor, true);
    }

    /**
     * 倒序
     *
     * @param keyExtractor 属性
     */
    default <U extends Comparable<? super U>> S desc(Function<? super T, ? extends U> keyExtractor) {
        return desc(true, keyExtractor);
    }

    /**
     * 指定逻辑倒序
     *
     * @param condition  装载条件
     * @param comparator 比较逻辑
     * @param nullFirst  是否null值前置
     */
    S desc(boolean condition, Comparator<? super T> comparator, boolean nullFirst);

    /**
     * 指定逻辑倒序
     *
     * @param comparator 比较逻辑
     * @param nullFirst  是否null值前置
     */
    default S desc(Comparator<? super T> comparator, boolean nullFirst) {
        return desc(true, comparator, nullFirst);
    }

    /**
     * 指定逻辑倒序
     *
     * @param condition  装载条件
     * @param comparator 比较逻辑
     */
    default S desc(boolean condition, Comparator<? super T> comparator) {
        return desc(condition, comparator, true);
    }

    /**
     * 指定逻辑倒序
     *
     * @param comparator 比较逻辑
     */
    default S desc(Comparator<? super T> comparator) {
        return desc(comparator, true);
    }
}

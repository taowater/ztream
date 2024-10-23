package com.taowater.ztream.op.sort;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 升序排序方法
 *
 * @author zhu56
 * @date 2024/10/24 00:46
 */
public interface Asc<T, S> {


    /**
     * 升序
     *
     * @param condition    装载条件
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     */
    <U extends Comparable<? super U>> S asc(boolean condition, Function<? super T, ? extends U> keyExtractor, boolean nullFirst);


    /**
     * 升序
     *
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     */
    default <U extends Comparable<? super U>> S asc(Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return asc(true, keyExtractor, nullFirst);
    }

    /**
     * 升序
     *
     * @param condition    装载条件
     * @param keyExtractor 属性
     */
    default <U extends Comparable<? super U>> S asc(boolean condition, Function<? super T, ? extends U> keyExtractor) {
        return asc(condition, keyExtractor, true);
    }

    /**
     * 升序
     *
     * @param keyExtractor 属性
     */
    default <U extends Comparable<? super U>> S asc(Function<? super T, ? extends U> keyExtractor) {
        return asc(true, keyExtractor);
    }

    /**
     * 指定逻辑升序
     *
     * @param condition  装载条件
     * @param comparator 比较逻辑
     * @param nullFirst  是否null值前置
     */
    S asc(boolean condition, Comparator<? super T> comparator, boolean nullFirst);

    /**
     * 指定逻辑升序
     *
     * @param comparator 比较逻辑
     * @param nullFirst  是否null值前置
     */
    default S asc(Comparator<? super T> comparator, boolean nullFirst) {
        return asc(true, comparator, nullFirst);
    }

    /**
     * 指定逻辑升序
     *
     * @param condition  装载条件
     * @param comparator 比较逻辑
     */
    default S asc(boolean condition, Comparator<? super T> comparator) {
        return asc(condition, comparator, true);
    }

    /**
     * 指定逻辑升序
     *
     * @param comparator 比较逻辑
     */
    default S asc(Comparator<? super T> comparator) {
        return asc(comparator, true);
    }
}

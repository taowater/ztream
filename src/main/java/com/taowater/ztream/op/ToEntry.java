package com.taowater.ztream.op;

import com.taowater.ztream.EntryZtream;
import com.taowater.ztream.IZtream;
import com.taowater.ztream.assist.Functions;
import com.taowater.ztream.assist.Spliterators;
import lombok.var;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 转为二元流的操作
 *
 * @author zhu56
 * @date 2025/06/25 01:11
 */
public interface ToEntry<T, S extends IZtream<T, S>> extends IZtream<T, S>, Distinct<T, S> {


    /**
     * 映射
     *
     * @param funK 键函数
     * @return {@link EntryZtream }<{@link K }, {@link T }>
     */
    default <K> EntryZtream<K, T> hash(Function<? super T, K> funK) {
        return hash(funK, Function.identity());
    }

    /**
     * 映射
     *
     * @param funK 键函数
     * @param funV 值函数
     * @return {@link EntryZtream }<{@link K }, {@link V }>
     */
    default <K, V> EntryZtream<K, V> hash(Function<? super T, K> funK, Function<? super T, V> funV) {
        return EntryZtream.of(distinct(funK).map(e -> Functions.entry(e, funK, funV)));
    }


    /**
     * 分组
     *
     * @param funK       键函数
     * @param funV       值函数
     * @param downstream 组元素处理
     * @return {@link EntryZtream }<{@link K }, {@link D }>
     */
    default <K, V, A, D> EntryZtream<K, D> group(Function<? super T, K> funK, Function<? super T, V> funV, Collector<? super V, A, D> downstream) {
        var spliterator = new Spliterators.GroupSpliterator<>(spliterator(), funK, funV, downstream);
        return EntryZtream.of(StreamSupport.stream(spliterator, isParallel()));
    }

    /**
     * 分组
     *
     * @param funK 键函数
     * @param funV 值函数
     * @return {@link EntryZtream }<{@link K }, {@link List }<{@link V }>>
     */
    default <K, V> EntryZtream<K, List<V>> group(Function<? super T, K> funK, Function<? super T, V> funV) {
        return group(funK, funV, Collectors.toList());
    }

    /**
     * 分组
     *
     * @param funK 键函数
     * @return {@link EntryZtream }<{@link K }, {@link List }<{@link T }>>
     */
    default <K> EntryZtream<K, List<T>> group(Function<? super T, K> funK) {
        return group(funK, Function.identity());
    }


}

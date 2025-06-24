package com.taowater.ztream.op;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 映射操作
 *
 * @author Zhu56
 * @since 0.0.1
 */
public interface ToMap<T> extends Stream<T> {


    /**
     * 映射
     *
     * @param funK 键方法
     * @return 映射结果
     */
    default <K> Map<K, T> toMap(Function<? super T, ? extends K> funK) {
        return this.toMap(funK, Function.identity());
    }

    /**
     * 映射
     *
     * @param funK       键方法
     * @param mapFactory map工厂
     * @return 映射结果
     */
    default <K, M extends Map<K, T>> M toMap(
            Function<? super T, ? extends K> funK,
            Supplier<M> mapFactory) {
        return this.toMap(funK, Function.identity(), mapFactory);
    }

    /**
     * 映射
     *
     * @param funK 键方法
     * @param funV 值方法
     * @return 映射结果
     */
    default <K, V> Map<K, V> toMap(
            Function<? super T, ? extends K> funK,
            Function<? super T, ? extends V> funV) {
        return this.toMap(funK, funV, HashMap::new);
    }

    /**
     * 映射
     *
     * @param funK       键方法
     * @param funV       值方法
     * @param mapFactory map工厂
     * @return 映射结果
     */
    default <K, V, M extends Map<K, V>> M toMap(
            Function<? super T, ? extends K> funK,
            Function<? super T, ? extends V> funV,
            Supplier<M> mapFactory) {
        return this.collect(mapFactory, (map, item) -> {
                    K key = null;
                    V value = null;
                    if (Objects.nonNull(item)) {
                        key = funK.apply(item);
                        value = funV.apply(item);
                    }
                    if (!map.containsKey(key)) {
                        map.put(key, value);
                    }
                },
                Map::putAll);
    }
}

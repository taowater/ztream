package com.taowater.ztream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 分组操作
 *
 * @author Zhu56
 * @date 2022/11/13 20:21:12
 */
interface ZGroupBy<T> extends Stream<T> {

    /**
     * 分组-缺省值类型、Map类型及组集合类型，默认为元素本身、HashMap及ArrayList
     *
     * @param funK funK
     * @return {@link Map}<{@link K}, {@link List}<{@link T}>>
     */
    default <K> Map<K, List<T>> groupBy(Function<? super T, ? extends K> funK) {
        return this.groupBy(funK, Function.identity());
    }

    /**
     * 分组-缺省值类型及组集合类型，默认为元素本身及ArrayList
     *
     * @param funK       分组依据
     * @param mapFactory map工厂
     * @return {@link M}
     */
    default <K, M extends Map<K, List<T>>> M groupBy(Function<? super T, ? extends K> funK, Supplier<M> mapFactory) {
        return this.groupBy(funK, mapFactory, Collectors.toList());
    }

    /**
     * 分组-缺省值类型及Map类型，默认为元素本身及HashMap
     *
     * @param funK       分组依据
     * @param downstream 下游
     * @return {@link M}
     */
    @SuppressWarnings("unchecked")
    default <K, A, D, M extends Map<K, D>> M groupBy(Function<? super T, ? extends K> funK, Collector<? super T, A, D> downstream) {
        return (M) this.groupBy(funK, Function.identity(), HashMap::new, downstream);
    }

    /**
     * 集团
     * 分组-缺省值类型，默认为元素本身
     *
     * @param funK       分组依据
     * @param mapFactory map工厂
     * @param downstream 下游
     * @return {@link M}
     */
    default <K, A, D, M extends Map<K, D>> M groupBy(Function<? super T, ? extends K> funK, Supplier<M> mapFactory, Collector<? super T, A, D> downstream) {
        return this.groupBy(funK, Function.identity(), mapFactory, downstream);
    }

    /**
     * 分组-缺省Map类型及组集合类型，默认为HashMap及ArrayList
     *
     * @param funK 分组依据
     * @param funV 值依据
     * @return {@link Map}<{@link K}, {@link List}<{@link V}>>
     */
    default <K, V> Map<K, List<V>> groupBy(Function<? super T, ? extends K> funK, Function<? super T, ? extends V> funV) {
        return this.groupBy(funK, funV, HashMap::new);
    }

    /**
     * 分组-缺省组集合类型，默认为ArrayList
     *
     * @param funK       分组依据
     * @param funV       值依据
     * @param mapFactory 提供的map
     * @return {@link Map}<{@link K}, {@link List}<{@link V}>>
     */
    default <K, V, M extends Map<K, List<V>>> M groupBy(Function<? super T, ? extends K> funK, Function<? super T, ? extends V> funV, Supplier<M> mapFactory) {
        return this.groupBy(funK, funV, mapFactory, Collectors.toList());
    }

    /**
     * 分组-缺省Map类型，默认为HashMap
     *
     * @param funK       分组依据
     * @param funV       值依据
     * @param downstream 下游操作(组集合的类型)
     * @return {@link M}
     */
    @SuppressWarnings("unchecked")
    default <K, V, A, D, M extends Map<K, D>> M groupBy(Function<? super T, ? extends K> funK, Function<? super T, ? extends V> funV, Collector<? super V, A, D> downstream) {
        return (M) this.groupBy(funK, funV, HashMap::new, downstream);
    }


    /**
     * 分组
     * 本方法是其余分组方法的基础，其余方法都是常见情形或默认缺省的重载
     * M<K,S<V>> 四个参数中：funK决定K；funV决定V；mapFactory决定M；downstream决定S
     *
     * @param funK       分组依据
     * @param funV       值依据
     * @param mapFactory 提供的map
     * @param downstream 下游操作(组集合的类型)
     * @return map
     */
    default <K, V, A, D, M extends Map<K, D>> M groupBy(Function<? super T, ? extends K> funK, Function<? super T, ? extends V> funV, Supplier<M> mapFactory, Collector<? super V, A, D> downstream) {
        return this.collect(ExCollectors.groupingBy(funK, mapFactory, ExCollectors.mapping(funV, downstream)));
    }

    /**
     * 两层分组
     *
     * @param funK  一重键
     * @param funK2 二重键
     * @return {@link M}
     */
    default <K, K2, M extends Map<K, Map<K2, List<T>>>> M groupBilayer(Function<? super T, ? extends K> funK, Function<? super T, ? extends K2> funK2) {
        return groupBilayer(funK, funK2, Function.identity());
    }

    /**
     * 两层分组
     *
     * @param funK  一重键
     * @param funK2 二重键
     * @param funV  值属性
     * @return {@link M}
     */
    @SuppressWarnings("unchecked")
    default <K, K2, V, M extends Map<K, Map<K2, List<V>>>> M groupBilayer(Function<? super T, ? extends K> funK, Function<? super T, ? extends K2> funK2, Function<? super T, ? extends V> funV) {
        return (M) this.reduce(new HashMap<K, Map<K2, List<V>>>(), (map, e) -> {
            Map<K2, List<V>> subMap = map.computeIfAbsent(funK.apply(e), k -> new HashMap<>());
            List<V> list = subMap.computeIfAbsent(funK2.apply(e), k -> new ArrayList<>());
            list.add(funV.apply(e));
            return map;
        }, (map1, map2) -> {
            map1.forEach((k, v) -> {
                Map<K2, List<V>> v2 = map2.get(k);
                map1.merge(k, v2, (o, n) -> {
                    o.forEach((sk, sv) -> {
                        List<V> sv2 = v2.get(sk);
                        o.merge(sk, sv2, (so, sn) -> {
                            so.addAll(sn);
                            return so;
                        });
                    });
                    return o;
                });
            });
            return map1;
        });
    }
}

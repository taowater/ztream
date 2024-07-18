package com.taowater.ztream.op;


import com.taowater.taol.core.util.ConvertUtil;
import com.taowater.ztream.Any;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 收集操作
 *
 * @author Zhu56
 * @since 0.0.1
 */
public interface Collect<T> extends Stream<T> {

    /**
     * 收集为集合
     *
     * @param collectionFactory 集合工厂
     * @return 集合
     */
    default <C extends Collection<T>> C collect(Supplier<? extends C> collectionFactory) {
        return collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * 收集属性
     *
     * @param fun               属性
     * @param collectionFactory 集合工厂
     * @return 集合
     */
    default <V, C extends Collection<V>> C collect(Function<? super T, ? extends V> fun, Supplier<? extends C> collectionFactory) {
        return this.map(e -> Any.of(e).get(fun)).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * 转换类型收集
     *
     * @param clazz             clazz
     * @param collectionFactory 收集工厂
     * @return 集合
     */
    default <N, C extends Collection<N>> C collect(Class<N> clazz, Supplier<? extends C> collectionFactory) {
        return this.map(e -> Any.of(e).get(o -> ConvertUtil.convert(o, clazz))).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * 收集属性
     *
     * @param fun 属性
     * @return 属性 ArrayList 集合
     */
    default <V> List<V> collect(Function<? super T, ? extends V> fun) {
        return this.collect(fun, ArrayList::new);
    }

    /**
     * 转换元素类型并收集
     *
     * @param clazz clazz
     * @return 转换 ArrayList 集合
     */
    default <N> List<N> collect(Class<N> clazz) {
        return this.collect(clazz, ArrayList::new);
    }

    /**
     * 收集为{@link ArrayList}
     *
     * @return ArrayList 集合
     */
    default List<T> toList() {
        return collect(Collectors.toList());
    }

    /**
     * 收集某属性为list
     *
     * @param fun 函数
     * @return 属性 ArrayList 集合
     */
    default <V> List<V> toList(Function<? super T, ? extends V> fun) {
        return this.collect(fun, ArrayList::new);
    }

    /**
     * 转换类型收集为list
     *
     * @param clazz clazz
     * @return 转换 ArrayList 集合
     */
    default <N> List<N> toList(Class<N> clazz) {
        return this.collect(clazz, ArrayList::new);
    }

    /**
     * 收集为{@link HashSet}
     *
     * @return HashSet 集合
     */
    default Set<T> toSet() {
        return collect(Collectors.toSet());
    }

    /**
     * 收集某属性为set
     *
     * @param fun 函数
     * @return 属性 HashSet 集合
     */
    default <V> Set<V> toSet(Function<? super T, ? extends V> fun) {
        return this.collect(fun, HashSet::new);
    }

    /**
     * 转换类型收集set
     *
     * @param clazz 类型
     * @return 转换 HashSet 集合
     */
    default <N> Set<N> toSet(Class<N> clazz) {
        return this.collect(clazz, HashSet::new);
    }
}

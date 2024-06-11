package io.github.taowater.ztream;


import io.github.taowater.util.ConvertUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流的收集操作
 *
 * @author Zhu56
 * @date 2022/11/13 19:12:35
 */
interface Collect<T> extends Stream<T> {

    /**
     * 转换成集合
     *
     * @param collectionFactory 集合工厂(可以是集合构造器)
     * @param <C>               集合类型
     * @return 集合
     */
    default <C extends Collection<T>> C collect(Supplier<? extends C> collectionFactory) {
        return collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * 收集属性
     *
     * @param fun 函数
     * @return {@link Set}<{@link V}>
     */
    default <V, C extends Collection<V>> C collect(Function<? super T, ? extends V> fun, Supplier<? extends C> collectionFactory) {
        return this.map(fun).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * 转换类型收集
     *
     * @param clazz clazz
     * @return {@link List}<{@link N}>
     */
    default <N, C extends Collection<N>> C collect(Class<N> clazz, Supplier<? extends C> collectionFactory) {
        return this.map(e -> ConvertUtil.convert(e, clazz)).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * 转换为{@link ArrayList}
     *
     * @return 集合
     */
    default List<T> toList() {
        return collect(Collectors.toList());
    }

    /**
     * 收集某个元素为list
     *
     * @param fun 函数
     * @return {@link Set}<{@link V}>
     */
    default <V> List<V> toList(Function<? super T, ? extends V> fun) {
        return this.collect(fun, ArrayList::new);
    }

    /**
     * 转换类型收集为list
     *
     * @param clazz clazz
     * @return {@link List}<{@link N}>
     */
    default <N> List<N> toList(Class<N> clazz) {
        return this.collect(clazz, ArrayList::new);
    }

    /**
     * 转换为HashSet
     *
     * @return 集合
     */
    default Set<T> toSet() {
        return collect(Collectors.toSet());
    }

    /**
     * 收集某个元素为set
     *
     * @param fun 函数
     * @return {@link Set}<{@link V}>
     */
    default <V> Set<V> toSet(Function<? super T, ? extends V> fun) {
        return this.collect(fun, HashSet::new);
    }

    /**
     * 转换类型收集set
     *
     * @param clazz clazz
     * @return {@link List}<{@link N}>
     */
    default <N> Set<N> toSet(Class<N> clazz) {
        return this.collect(clazz, HashSet::new);
    }
}

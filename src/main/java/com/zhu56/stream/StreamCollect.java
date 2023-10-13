package com.zhu56.stream;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流的收集操作
 *
 * @author 朱滔
 * @date 2022/11/13 19:12:35
 */
public interface StreamCollect<T> extends Stream<T> {

    /**
     * 转换成集合
     *
     * @param collectionFactory 集合工厂(可以是集合构造器)
     * @param <C>               集合类型
     * @return 集合
     */
    default <C extends Collection<T>> C collect(Supplier<C> collectionFactory) {
        return collect(Collectors.toCollection(collectionFactory));
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
     * 转换为HashSet
     *
     * @return 集合
     */
    default Set<T> toSet() {
        return collect(Collectors.toSet());
    }

    /**
     * 收集某个元素为无重复集合
     * @param fun 函数
     * @return {@link Set}<{@link V}>
     */
    default <V> Set<V> toSet(Function<T, V> fun) {
        return Ztream.of(this).map(fun).toSet();
    }

    /**
     * 收集
     *
     * @param fun 函数
     * @return {@link List}<{@link V}>
     */
    default <V> List<V> collect(Function<T, V> fun) {
        return Ztream.of(this).map(fun).nonNull().toList();
    }

    /**
     * 转换类型收集
     *
     * @param clazz clazz
     * @return {@link List}<{@link N}>
     */
    default <N> List<N> collect(Class<N> clazz) {
        return Ztream.of(this).convert(clazz).toList();
    }
}

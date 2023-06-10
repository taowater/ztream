package com.zhu56.stream;


import java.util.*;
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
     * 收集
     *
     * @param fun 函数
     * @return {@link List}<{@link V}>
     */
    default <V> List<V> collect(Function<T, V> fun) {
        return Ztream.of(this).map(fun).filter(Objects::nonNull).toList();
    }
}

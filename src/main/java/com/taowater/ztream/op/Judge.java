package com.taowater.ztream.op;

import com.taowater.ztream.Any;
import com.taowater.ztream.IZtream;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 判断操作
 *
 * @author zhu56
 * @date @since 0.1.13
 */
public interface Judge<T, S extends IZtream<T, S>> extends IZtream<T, S> {

    /**
     * 判断元素是否重复
     *
     * @return 判断结果
     */
    default boolean hadRepeat() {
        Set<T> set = new HashSet<>();
        return anyMatch(x -> !set.add(x));
    }

    /**
     * 判断元素某属性是重复
     *
     * @param fun 属性
     * @return 判断结果
     */
    default <V> boolean hadRepeat(Function<? super T, ? extends V> fun) {
        Set<V> set = new HashSet<>();
        return anyMatch(x -> !set.add(Any.of(x).get(fun)));
    }

    /**
     * 判断元素指定属性是否任一匹配
     *
     * @param fun       属性
     * @param predicate 条件
     * @return 判断结果
     */
    default <V> boolean anyMatch(Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        return judge(Stream::anyMatch, fun, predicate);
    }

    /**
     * 判断元素指定属性是否全部匹配
     *
     * @param fun       属性
     * @param predicate 条件
     * @return 判断结果
     */
    default <V> boolean allMatch(Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        return judge(Stream::allMatch, fun, predicate);
    }

    /**
     * 判断元素指定属性是否全都不匹配
     *
     * @param fun       属性
     * @param predicate 条件
     * @return 判断结果
     */
    default <V> boolean noneMatch(Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        return judge(Stream::noneMatch, fun, predicate);
    }

    /**
     * 判断
     *
     * @param handle    指定一种判断方式
     * @param fun       属性
     * @param predicate 条件
     * @return 判断结果
     */
    default <V> boolean judge(BiPredicate<? super S, Predicate<? super T>> handle, Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        return handle.test(wrap(this), e -> predicate.test(Any.of(e).get(fun)));
    }

    /**
     * 是否为空
     *
     * @return 判断结果
     */
    default boolean isEmpty() {
        return !isNotEmpty();
    }

    /**
     * 是否不为空
     *
     * @return 判断结果
     */
    default boolean isNotEmpty() {
        return iterator().hasNext();
    }

}

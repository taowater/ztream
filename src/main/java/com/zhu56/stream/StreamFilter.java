package com.zhu56.stream;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 流的过滤操作
 * 类比mybatis-plus的过滤
 *
 * @author 朱滔
 * @date 2022/11/13 20:21:12
 */
interface StreamFilter<T> extends Stream<T> {

    /**
     * 等值操作
     *
     * @param fun   字段
     * @param value 值
     * @return {@link Ztream}<{@link T}>
     */
    default <V> Ztream<T> eq(Function<T, V> fun, V value) {
        return Ztream.of(this).filter(e -> Objects.equals(fun.apply(e), value));
    }

    /**
     * 过滤指定字符属性以value开头的元素
     * value可指定多个，任意满足即可
     *
     * @param fun    有趣
     * @param values 价值
     * @return {@link Ztream}<{@link T}>
     */
    default Ztream<T> rightLike(Function<T, String> fun, String... values) {
        return Ztream.of(this).filter(e -> {
            String str = fun.apply(e);
            if (Objects.isNull(str)) {
                return false;
            }
            return Ztream.of(values).anyMatch(str::startsWith);
        });
    }

    /**
     * 不等操作
     *
     * @param fun   字段
     * @param value 价
     * @return {@link Ztream}<{@link T}>
     */
    default <V> Ztream<T> ne(Function<T, V> fun, V value) {
        return Ztream.of(this).filter(e -> !Objects.equals(fun.apply(e), value));
    }

    /**
     * in操作
     *
     * @param fun    字段
     * @param values 值
     * @return {@link Ztream}<{@link T}>
     */
    default <V> Ztream<T> in(Function<T, V> fun, Collection<V> values) {
        return Ztream.of(this).filter(e -> values.contains(fun.apply(e)));
    }

    /**
     * notin操作
     *
     * @param fun    字段
     * @param values 值
     * @return {@link Ztream}<{@link T}>
     */
    default <V> Ztream<T> notIn(Function<T, V> fun, Collection<V> values) {
        return Ztream.of(this).filter(e -> !values.contains(fun.apply(e)));
    }


    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     * @return {@link Ztream}<{@link T}>
     */
    default Ztream<T> in(Collection<T> c) {
        return Ztream.of(this).filter(c::contains);
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param c c
     * @return {@link Ztream}<{@link T}>
     */
    default Ztream<T> notIn(Collection<T> c) {
        return Ztream.of(this).filter(e -> !c.contains(e));
    }

    /**
     * 过滤某字段为空的元素
     *
     * @param fun 函数
     * @return {@link Ztream}<{@link T}>
     */
    default Ztream<T> isNull(Function<T, ?> fun) {
        return Ztream.of(this).filter(e -> Objects.isNull(fun.apply(e)));
    }

    /**
     * 过滤某字段不为空的元素
     *
     * @param fun 字段
     * @return {@link Ztream}<{@link T}>
     */
    default Ztream<T> nonNull(Function<T, ?> fun) {
        return Ztream.of(this).filter(e -> Objects.nonNull(fun.apply(e)));
    }

    /**
     * 过滤元素非空
     *
     * @return {@link Ztream}<{@link T}>
     */
    default Ztream<T> nonNull() {
        return Ztream.of(this).filter(Objects::nonNull);
    }


}

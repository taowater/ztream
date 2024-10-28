package com.taowater.ztream.op.filter;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 比较操作
 *
 * @author zhu56
 * @date 2024/10/24 23:48
 */
public interface Compare<T, W> extends ConditionCompare<T, W> {

    W filter(Predicate<? super T> predicate);

    /**
     * 过滤
     *
     * @param fun       属性
     * @param predicate 判断函数
     */
    default <V> W filter(Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        return filter(true, fun, predicate);
    }

    /**
     * 等值操作
     *
     * @param fun   属性
     * @param value 值
     */
    default <V> W eq(Function<? super T, ? extends V> fun, V value) {
        return eq(true, fun, value);
    }


    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    default <V> W in(Function<? super T, ? extends V> fun, Collection<? extends V> values) {
        return in(true, fun, values);
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    default <V> W in(Function<? super T, ? extends V> fun, V... values) {
        return in(true, fun, values);
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    default W in(Collection<? extends T> c) {
        return in(true, c);
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    @SuppressWarnings("all")
    default W in(T... c) {
        return in(true, c);
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param c c
     */
    default W notIn(Collection<? extends T> c) {
        return notIn(true, c);
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param values 指定值
     */
    @SuppressWarnings("all")
    default W notIn(T... values) {
        return notIn(true, values);
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    default <V> W notIn(Function<? super T, ? extends V> fun, Collection<? extends V> values) {
        return notIn(true, fun, values);
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    default <V> W notIn(Function<? super T, ? extends V> fun, V... values) {
        return notIn(true, fun, values);
    }

    /**
     * 过滤为空的元素
     */
    default W isNull() {
        return isNull(true);
    }

    /**
     * 过滤元素非空
     */
    default W nonNull() {
        return nonNull(true);
    }

    /**
     * 过滤某属性为空的元素
     *
     * @param fun 函数
     */
    default W isNull(Function<? super T, ?> fun) {
        return isNull(true, fun);
    }


    /**
     * 过滤某属性不为空的元素
     *
     * @param fun 属性
     */
    default W nonNull(Function<? super T, ?> fun) {
        return nonNull(true, fun);
    }

    /**
     * 过滤某字符串属性为空的元素
     *
     * @param fun 属性
     */
    default W isEmpty(Function<? super T, ?> fun) {
        return isEmpty(true, fun);
    }

    /**
     * 过滤某字符串属性不为空的元素
     *
     * @param fun 属性
     */
    default W nonEmpty(Function<? super T, ?> fun) {
        return nonEmpty(true, fun);
    }

    /**
     * 过滤某字符串属性为空白的元素
     *
     * @param fun 属性
     */
    default W isBlank(Function<? super T, CharSequence> fun) {
        return isBlank(true, fun);
    }

    /**
     * 过滤某字符串属性不为空白的元素
     *
     * @param fun 属性
     */
    default W nonBlank(Function<? super T, CharSequence> fun) {
        return nonBlank(true, fun);
    }

    /**
     * 小于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W lt(Function<? super T, ? extends N> fun, N value) {
        return lt(true, fun, value);
    }

    /**
     * 小于等于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W le(Function<? super T, ? extends N> fun, N value) {
        return le(true, fun, value);
    }

    /**
     * 大于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W gt(Function<? super T, ? extends N> fun, N value) {
        return gt(true, fun, value);
    }

    /**
     * 大于等于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W ge(Function<? super T, ? extends N> fun, N value) {
        return ge(true, fun, value);
    }

    /**
     * 区间
     *
     * @param fun        属性
     * @param leftValue  左值
     * @param rightValue 右值
     */
    default <N extends Comparable<? super N>> W between(Function<? super T, ? extends N> fun, N leftValue, N rightValue) {
        return between(true, fun, leftValue, rightValue);
    }

    /**
     * 过滤指定字符属性以value开头的元素
     *
     * @param fun   属性
     * @param value 值
     */
    default W rightLike(Function<? super T, String> fun, String value) {
        return rightLike(true, fun, value);
    }

    default W like(Function<? super T, String> fun, String value) {
        return like(true, fun, value);
    }
}

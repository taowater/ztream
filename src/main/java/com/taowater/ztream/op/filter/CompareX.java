package com.taowater.ztream.op.filter;

import com.taowater.taol.core.util.EmptyUtil;

import java.util.Collection;
import java.util.function.Function;

/**
 * 比较操作
 *
 * @author zhu56
 */
@SuppressWarnings("unused")
public interface CompareX<T, W> extends ConditionCompare<T, W> {

    /**
     * 等值操作
     *
     * @param fun   属性
     * @param value 值
     */
    default <V> W eqX(Function<? super T, ? extends V> fun, V value) {
        return eq(EmptyUtil.isNotEmpty(value), fun, value);
    }


    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    default <V> W inX(Function<? super T, ? extends V> fun, Collection<? extends V> values) {
        return in(EmptyUtil.isNotEmpty(values), fun, values);
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    default <V> W inX(Function<? super T, ? extends V> fun, V... values) {
        return in(EmptyUtil.isNotEmpty(values), fun, values);
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param values values
     */
    default W inX(Collection<? extends T> values) {
        return in(EmptyUtil.isNotEmpty(values), values);
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    @SuppressWarnings("all")
    default W inX(T... values) {
        return in(EmptyUtil.isNotEmpty(values), values);
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param value value
     */
    default W notInX(Collection<? extends T> value) {
        return notIn(EmptyUtil.isNotEmpty(value), value);
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param values 指定值
     */
    @SuppressWarnings("all")
    default W notInX(T... values) {
        return notIn(EmptyUtil.isNotEmpty(values), values);
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    default <V> W notInX(Function<? super T, ? extends V> fun, Collection<? extends V> values) {
        return notIn(EmptyUtil.isNotEmpty(values), fun, values);
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    default <V> W notInX(Function<? super T, ? extends V> fun, V... values) {
        return notIn(EmptyUtil.isNotEmpty(values), fun, values);
    }


    /**
     * 小于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W ltX(Function<? super T, ? extends N> fun, N value) {
        return lt(EmptyUtil.isNotEmpty(value), fun, value);
    }

    /**
     * 小于等于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W leX(Function<? super T, ? extends N> fun, N value) {
        return le(EmptyUtil.isNotEmpty(value), fun, value);
    }

    /**
     * 大于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W gtX(Function<? super T, ? extends N> fun, N value) {
        return gt(EmptyUtil.isNotEmpty(value), fun, value);
    }

    /**
     * 大于等于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W geX(Function<? super T, ? extends N> fun, N value) {
        return ge(EmptyUtil.isNotEmpty(value), fun, value);
    }

    /**
     * 区间
     *
     * @param fun        属性
     * @param leftValue  左值
     * @param rightValue 右值
     */
    default <N extends Comparable<? super N>> W betweenX(Function<? super T, ? extends N> fun, N leftValue, N rightValue) {
        return between(EmptyUtil.isAllNotEmpty(leftValue, rightValue), fun, leftValue, rightValue);
    }

    /**
     * 过滤指定字符属性以value开头的元素
     *
     * @param fun   属性
     * @param value 值
     */
    default W rightLikeX(Function<? super T, String> fun, String value) {
        return rightLike(EmptyUtil.isNotEmpty(value), fun, value);
    }

    default W likeX(Function<? super T, String> fun, String value) {
        return like(EmptyUtil.isNotEmpty(value), fun, value);
    }
}

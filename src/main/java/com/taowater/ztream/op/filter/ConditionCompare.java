package com.taowater.ztream.op.filter;

import com.taowater.taol.core.util.EmptyUtil;
import com.taowater.ztream.Any;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 比较操作
 *
 * @author zhu56
 * @date 2024/10/24 23:48
 */
@SuppressWarnings("unchecked")
public interface ConditionCompare<T, W> {

    W filter(boolean condition, Predicate<? super T> predicate);

    /**
     * 过滤
     *
     * @param fun       属性
     * @param predicate 判断函数
     */
    default <V> W filter(boolean condition, Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        return filter(condition, e -> predicate.test(Any.of(e).get(fun)));
    }

    /**
     * 等值操作
     *
     * @param fun   属性
     * @param value 值
     */
    default <V> W eq(boolean condition, Function<? super T, ? extends V> fun, V value) {
        return filter(condition, e -> Objects.equals(value, Any.of(e).get(fun)));
    }


    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    default <V> W in(boolean condition, Function<? super T, ? extends V> fun, Collection<? extends V> values) {
        return filter(condition, fun, Any.of(values).orElse(new ArrayList<>())::contains);
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    default <V> W in(boolean condition, Function<? super T, ? extends V> fun, V... values) {
        return in(condition, fun, Any.of(values).map(Stream::of).orElse(Stream.empty()).collect(Collectors.toSet()));
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    default W in(boolean condition, Collection<? extends T> c) {
        return filter(condition, Any.of(c).orElse(new ArrayList<>())::contains);
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    @SuppressWarnings("all")
    default W in(boolean condition, T... values) {
        return in(condition, Any.of(values).map(Stream::of).orElse(Stream.empty()).collect(Collectors.toSet()));
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param c c
     */
    default W notIn(boolean condition, Collection<? extends T> c) {
        return filter(condition, e -> !Any.of(c).orElse(new ArrayList<>()).contains(e));
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param values 指定值
     */
    @SuppressWarnings("all")
    default W notIn(boolean condition, T... values) {
        return notIn(condition, Any.of(values).map(Stream::of).orElse(Stream.empty()).collect(Collectors.toSet()));
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    default <V> W notIn(boolean condition, Function<? super T, ? extends V> fun, Collection<? extends V> values) {
        return filter(condition, fun, v -> !Any.of(values).orElse(new ArrayList<>()).contains(v));
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    default <V> W notIn(boolean condition, Function<? super T, ? extends V> fun, V... values) {
        return notIn(condition, fun, Any.of(values).map(Stream::of).orElse(Stream.empty()).collect(Collectors.toSet()));
    }

    /**
     * 过滤为空的元素
     */
    default W isNull(boolean condition) {
        return filter(condition, Objects::isNull);
    }

    /**
     * 过滤元素非空
     */
    default W nonNull(boolean condition) {
        return filter(condition, Objects::nonNull);
    }

    /**
     * 过滤某属性为空的元素
     *
     * @param fun 函数
     */
    default W isNull(boolean condition, Function<? super T, ?> fun) {
        return filter(condition, fun, Objects::isNull);
    }


    /**
     * 过滤某属性不为空的元素
     *
     * @param fun 属性
     */
    default W nonNull(boolean condition, Function<? super T, ?> fun) {
        return filter(condition, fun, Objects::nonNull);
    }

    /**
     * 过滤某字符串属性为空的元素
     *
     * @param fun 属性
     */
    default W isEmpty(boolean condition, Function<? super T, ?> fun) {
        return filter(condition, fun, EmptyUtil::isEmpty);
    }

    /**
     * 过滤某字符串属性不为空的元素
     *
     * @param fun 属性
     */
    default W nonEmpty(boolean condition, Function<? super T, ?> fun) {
        return filter(condition, fun, EmptyUtil::isNotEmpty);
    }

    /**
     * 过滤某字符串属性为空白的元素
     *
     * @param fun 属性
     */
    default W isBlank(boolean condition, Function<? super T, CharSequence> fun) {
        return filter(condition, fun, s -> EmptyUtil.isEmpty(s) || s.toString().trim().isEmpty());
    }

    /**
     * 过滤某字符串属性不为空白的元素
     *
     * @param fun 属性
     */
    default W nonBlank(boolean condition, Function<? super T, CharSequence> fun) {
        return filter(condition, fun, s -> EmptyUtil.isNotEmpty(s) && !s.toString().trim().isEmpty());
    }

    /**
     * 小于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W lt(boolean condition, Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return filter(condition, e -> {
            N v = Any.of(e).get(fun);
            if (Objects.isNull(v)) {
                return false;
            }
            return v.compareTo(value) < 0;
        });
    }

    /**
     * 小于等于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W le(boolean condition, Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return filter(condition, e -> {
            N v = Any.of(e).get(fun);
            if (Objects.isNull(v)) {
                return false;
            }
            return v.compareTo(value) <= 0;
        });
    }

    /**
     * 大于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W gt(boolean condition, Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return filter(condition, e -> {
            N v = Any.of(e).get(fun);
            if (Objects.isNull(v)) {
                return false;
            }
            return v.compareTo(value) > 0;
        });
    }

    /**
     * 大于等于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W ge(boolean condition, Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return filter(condition, e -> {
            N v = Any.of(e).get(fun);
            if (Objects.isNull(v)) {
                return false;
            }
            return v.compareTo(value) >= 0;
        });
    }

    /**
     * 区间
     *
     * @param fun        属性
     * @param leftValue  左值
     * @param rightValue 右值
     */
    default <N extends Comparable<? super N>> W between(boolean condition, Function<? super T, ? extends N> fun, N leftValue, N rightValue) {

        if (!condition || EmptyUtil.isAllEmpty(leftValue, rightValue)) {
            return filter(true, e -> true);
        }
        Predicate<T> predicate = e -> true;
        if (Objects.nonNull(leftValue)) {
            predicate = predicate.and(e -> {
                N v = Any.of(e).get(fun);
                if (Objects.isNull(v)) {
                    return false;
                }
                return v.compareTo(leftValue) >= 0;
            });
        }
        if (Objects.nonNull(rightValue)) {
            predicate = predicate.and(e -> {
                N v = Any.of(e).get(fun);
                if (Objects.isNull(v)) {
                    return false;
                }
                return v.compareTo(rightValue) <= 0;
            });
        }
        return filter(true, predicate);
    }

    /**
     * 过滤指定字符属性以value开头的元素
     *
     * @param fun   属性
     * @param value 值
     */
    default W rightLike(boolean condition, Function<? super T, String> fun, String value) {
        return filter(condition, e -> {
            String str = Any.of(e).get(fun);
            if (Objects.isNull(str)) {
                return Objects.isNull(value);
            }
            if (Objects.isNull(value)) {
                return false;
            }
            return str.startsWith(value);
        });
    }

    default W like(boolean condition, Function<? super T, String> fun, String value) {
        return filter(condition, e -> {
            String str = Any.of(e).get(fun);
            if (Objects.isNull(str)) {
                return Objects.isNull(value);
            }
            if (Objects.isNull(value)) {
                return false;
            }
            return str.contains(value);
        });
    }
}

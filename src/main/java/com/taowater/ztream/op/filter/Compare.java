package com.taowater.ztream.op.filter;

import com.taowater.taol.core.util.EmptyUtil;
import com.taowater.ztream.Any;
import org.dromara.hutool.core.text.StrValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
public interface Compare<T, W> {

    W filter(Predicate<? super T> predicate);

    /**
     * 过滤
     *
     * @param fun       属性
     * @param predicate 判断函数
     */
    default <V> W filter(Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        return filter(e -> predicate.test(Any.of(e).get(fun)));
    }

    /**
     * 等值操作
     *
     * @param fun   属性
     * @param value 值
     */
    default <V> W eq(Function<? super T, ? extends V> fun, V value) {
        return filter(e -> Objects.equals(value, Any.of(e).get(fun)));
    }


    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    default <V, C extends Collection<? extends V>> W in(Function<? super T, ? extends V> fun, C values) {
        if (EmptyUtil.isEmpty(values)) {
            return filter(e -> false);
        }
        return filter(fun, values::contains);
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    default <V> W in(Function<? super T, ? extends V> fun, V... values) {
        if (EmptyUtil.isEmpty(values)) {
            return filter(e -> false);
        }
        return in(fun, Stream.of(values).collect(Collectors.toSet()));
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    default <C extends Collection<? extends T>> W in(C c) {
        if (EmptyUtil.isEmpty(c)) {
            return filter(e -> false);
        }
        return filter(c::contains);
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    @SuppressWarnings("all")
    default W in(T... c) {
        return in(Stream.of(c).collect(Collectors.toSet()));
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param c c
     */
    default <C extends Collection<? extends T>> W notIn(C c) {
        if (EmptyUtil.isEmpty(c)) {
            return filter(e -> true);
        }
        return filter(e -> !c.contains(e));
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param values 指定值
     */
    @SuppressWarnings("all")
    default W notIn(T... values) {
        return notIn(Stream.of(values).collect(Collectors.toSet()));
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    default <V, C extends Collection<? extends V>> W notIn(Function<? super T, ? extends V> fun, C values) {
        if (EmptyUtil.isEmpty(values)) {
            return filter(e -> true);
        }
        return filter(fun, v -> !values.contains(v));
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    default <V> W notIn(Function<? super T, ? extends V> fun, V... values) {
        return notIn(fun, new HashSet<>(Arrays.asList(values)));
    }

    /**
     * 过滤为空的元素
     */
    default W isNull() {
        return filter(Objects::isNull);
    }

    /**
     * 过滤元素非空
     */
    default W nonNull() {
        return filter(Objects::nonNull);
    }

    /**
     * 过滤某属性为空的元素
     *
     * @param fun 函数
     */
    default W isNull(Function<? super T, ?> fun) {
        return filter(fun, Objects::isNull);
    }


    /**
     * 过滤某属性不为空的元素
     *
     * @param fun 属性
     */
    default W nonNull(Function<? super T, ?> fun) {
        return filter(fun, Objects::nonNull);
    }

    /**
     * 过滤某字符串属性为空的元素
     *
     * @param fun 属性
     */
    default W isEmpty(Function<? super T, CharSequence> fun) {
        return filter(fun, EmptyUtil::isEmpty);
    }

    /**
     * 过滤某字符串属性不为空的元素
     *
     * @param fun 属性
     */
    default W nonEmpty(Function<? super T, CharSequence> fun) {
        return filter(fun, EmptyUtil::isNotEmpty);
    }

    /**
     * 过滤某字符串属性为空白的元素
     *
     * @param fun 属性
     */
    default W isBlank(Function<? super T, CharSequence> fun) {
        return filter(fun, StrValidator::isBlank);
    }

    /**
     * 过滤某字符串属性不为空白的元素
     *
     * @param fun 属性
     */
    default W nonBlank(Function<? super T, CharSequence> fun) {
        return filter(fun, StrValidator::isNotBlank);
    }

    /**
     * 小于
     *
     * @param fun   属性
     * @param value 值
     */
    default <N extends Comparable<? super N>> W lt(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return filter(e -> {
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
    default <N extends Comparable<? super N>> W le(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return filter(e -> {
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
    default <N extends Comparable<? super N>> W gt(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return filter(e -> {
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
    default <N extends Comparable<? super N>> W ge(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return filter(e -> {
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
    default <N extends Comparable<? super N>> W between(Function<? super T, ? extends N> fun, N leftValue, N rightValue) {
        if (EmptyUtil.isAllEmpty(leftValue, rightValue)) {
            return filter(e -> true);
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
        return filter(predicate);
    }

    /**
     * 过滤指定字符属性以value开头的元素
     *
     * @param fun   属性
     * @param value 值
     */
    default W rightLike(Function<? super T, String> fun, String value) {
        return filter(e -> {
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

    default W like(Function<? super T, String> fun, String value) {
        return filter(e -> {
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

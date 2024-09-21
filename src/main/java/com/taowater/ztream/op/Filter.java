package com.taowater.ztream.op;

import com.taowater.taol.core.util.EmptyUtil;
import com.taowater.ztream.Any;
import com.taowater.ztream.IZtream;
import com.taowater.ztream.assist.Wrapper;
import org.dromara.hutool.core.text.StrValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 过滤操作
 *
 * @author zhu56
 * @see 0.0.3
 */
public interface Filter<T, S extends IZtream<T, S>> extends IZtream<T, S> {

    /**
     * 等值操作
     *
     * @param fun   属性
     * @param value 值
     * @return 新流
     */
    default <V> S eq(Function<? super T, ? extends V> fun, V value) {
        return filter(fun, v -> Objects.equals(v, value));
    }

    /**
     * 小于
     *
     * @param fun   属性
     * @param value 值
     * @return 新流
     */
    default <N extends Comparable<? super N>> S lt(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return wrap(nonNull(fun).filter(e -> fun.apply(e).compareTo(value) < 0));
    }

    /**
     * 小于等于
     *
     * @param fun   属性
     * @param value 值
     * @return 新流
     */
    default <N extends Comparable<? super N>> S le(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return wrap(nonNull(fun).filter(e -> fun.apply(e).compareTo(value) <= 0));
    }

    /**
     * 大于
     *
     * @param fun   属性
     * @param value 值
     * @return 新流
     */
    default <N extends Comparable<? super N>> S gt(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return wrap(nonNull(fun).filter(e -> fun.apply(e).compareTo(value) > 0));
    }

    /**
     * 大于等于
     *
     * @param fun   属性
     * @param value 值
     * @return 新流
     */
    default <N extends Comparable<? super N>> S ge(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return wrap(nonNull(fun).filter(e -> fun.apply(e).compareTo(value) >= 0));
    }

    /**
     * 区间
     *
     * @param fun        属性
     * @param leftValue  左值
     * @param rightValue 右值
     * @return 新流
     */
    @SuppressWarnings("unchecked")
    default <N extends Comparable<? super N>> S between(Function<? super T, ? extends N> fun, N leftValue, N rightValue) {
        if (EmptyUtil.isAllEmpty(leftValue, rightValue)) {
            return wrap(this);
        }
        return nonNull(fun)
                .handle(Objects.nonNull(leftValue), z -> ((Filter<T, S>) z).ge(fun, leftValue))
                .handle(Objects.nonNull(rightValue), z -> ((Filter<T, S>) z).le(fun, rightValue));
    }

    /**
     * 过滤指定字符属性以value开头的元素
     *
     * @param fun   属性
     * @param value 值
     * @return 新流
     */
    default S rightLike(Function<? super T, String> fun, String value) {
        return filter(e -> {
            String str = Any.of(e).get(fun);
            if (EmptyUtil.isEmpty(str)) {
                return EmptyUtil.isEmpty(value);
            }
            if (EmptyUtil.isEmpty(value)) {
                return true;
            }
            return str.startsWith(value);
        });
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     * @return 新流
     */
    default <V, C extends Collection<? extends V>> S in(Function<? super T, ? extends V> fun, C values) {
        return EmptyUtil.isEmpty(values) ? wrap(Stream.empty()) : filter(fun, values::contains);
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     * @return 新流
     */
    @SuppressWarnings("unchecked")
    default <V> S in(Function<? super T, ? extends V> fun, V... values) {
        if (EmptyUtil.isEmpty(values)) {
            return wrap(Stream.empty());
        }
        return in(fun, Stream.of(values).collect(Collectors.toSet()));
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     * @return 新流
     */
    default <V, C extends Collection<? extends V>> S notIn(Function<? super T, ? extends V> fun, C values) {
        return EmptyUtil.isEmpty(values) ? wrap(this) : filter(fun, v -> !values.contains(v));
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     * @return 新流
     */
    @SuppressWarnings("unchecked")
    default <V> S notIn(Function<? super T, ? extends V> fun, V... values) {
        return notIn(fun, new HashSet<>(Arrays.asList(values)));
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     * @return 新流
     */
    default <C extends Collection<? extends T>> S in(C c) {
        return EmptyUtil.isEmpty(c) ? wrap(Stream.empty()) : filter(c::contains);
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     * @return 新流
     */
    @SuppressWarnings("all")
    default S in(T... c) {
        return in(Stream.of(c).collect(Collectors.toSet()));
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param c c
     * @return 新流
     */
    default <C extends Collection<? extends T>> S notIn(C c) {
        return EmptyUtil.isEmpty(c) ? wrap(this) : filter(e -> !c.contains(e));
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param values 指定值
     * @return 新流
     */
    @SuppressWarnings("all")
    default S notIn(T... values) {
        return notIn(Stream.of(values).collect(Collectors.toSet()));
    }

    /**
     * 过滤某属性为空的元素
     *
     * @param fun 函数
     * @return 新流
     */
    default S isNull(Function<? super T, ?> fun) {
        return this.filter(fun, Objects::isNull);
    }

    /**
     * 过滤为空的元素
     *
     * @return {@link S }
     */
    default S isNull() {
        return this.filter(Objects::isNull);
    }

    /**
     * 过滤元素非空
     *
     * @return 新流
     */
    default S nonNull() {
        return filter(Objects::nonNull);
    }

    /**
     * 过滤某属性不为空的元素
     *
     * @param fun 属性
     * @return 新流
     */
    default S nonNull(Function<? super T, ?> fun) {
        return filter(fun, Objects::nonNull);
    }

    /**
     * 过滤某字符串属性为空的元素
     *
     * @param fun 属性
     * @return 新流
     */
    default S isEmpty(Function<? super T, CharSequence> fun) {
        return filter(fun, EmptyUtil::isEmpty);
    }

    /**
     * 过滤某字符串属性不为空的元素
     *
     * @param fun 属性
     * @return 新流
     */
    default S nonEmpty(Function<? super T, CharSequence> fun) {
        return filter(fun, EmptyUtil::isNotEmpty);
    }

    /**
     * 过滤某字符串属性为空白的元素
     *
     * @param fun 属性
     * @return 新流
     */
    default S isBlank(Function<? super T, CharSequence> fun) {
        return filter(fun, StrValidator::isBlank);
    }

    /**
     * 过滤某字符串属性不为空白的元素
     *
     * @param fun 属性
     * @return 新流
     */
    default S nonBlank(Function<? super T, CharSequence> fun) {
        return filter(fun, StrValidator::isNotBlank);
    }

    /**
     * 过滤
     *
     * @param fun       属性
     * @param predicate 判断函数
     * @return 新流
     */
    default <V> S filter(Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        return filter(e -> predicate.test(Any.of(e).get(fun)));
    }

    /**
     * 条件过滤
     *
     * @param consumer 消费者
     * @return {@link S }
     */
    default S query(Consumer<Wrapper<T>> consumer) {
        Wrapper<T> wrapper = new Wrapper<>();
        consumer.accept(wrapper);
        return wrap(filter(e -> e, wrapper.getCondition()));
    }
}

package com.taowater.ztream.assist;


import com.taowater.taol.core.util.EmptyUtil;
import com.taowater.ztream.Any;
import lombok.Getter;
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
 * 条件
 *
 * @author zhu56
 * @date 2024/09/16 01:43
 */
@Getter
public class Wrapper<T> {

    private Predicate<T> condition = o -> true;

    private void andPre(Predicate<T> predicate) {
        this.condition = predicate.and(predicate);
    }

    /**
     * 且
     */
    public Wrapper<T> and(Consumer<Wrapper<T>> consumer) {
        Wrapper<T> wrapper = new Wrapper<>();
        consumer.accept(wrapper);
        this.condition = condition.and(wrapper.condition);
        return this;
    }

    /**
     * 或
     */
    public Wrapper<T> or(Consumer<Wrapper<T>> consumer) {
        Wrapper<T> wrapper = new Wrapper<>();
        consumer.accept(wrapper);
        this.condition = condition.or(wrapper.condition);
        return this;
    }

    /**
     * 等值操作
     *
     * @param fun   属性
     * @param value 值
     */
    public <V> Wrapper<T> eq(Function<? super T, ? extends V> fun, V value) {
        andPre(e -> Objects.equals(value, Any.of(e).get(fun)));
        return this;
    }

    /**
     * 小于
     *
     * @param fun   属性
     * @param value 值
     */
    public <N extends Comparable<? super N>> Wrapper<T> lt(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        andPre(e -> {
            N v = Any.of(e).get(fun);
            if (Objects.isNull(v)) {
                return false;
            }
            return v.compareTo(value) < 0;
        });
        return this;
    }

    /**
     * 小于等于
     *
     * @param fun   属性
     * @param value 值
     */
    public <N extends Comparable<? super N>> Wrapper<T> le(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        andPre(e -> {
            N v = Any.of(e).get(fun);
            if (Objects.isNull(v)) {
                return false;
            }
            return v.compareTo(value) <= 0;
        });
        return this;
    }

    /**
     * 大于
     *
     * @param fun   属性
     * @param value 值
     */
    public <N extends Comparable<? super N>> Wrapper<T> gt(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        andPre(e -> {
            N v = Any.of(e).get(fun);
            if (Objects.isNull(v)) {
                return false;
            }
            return v.compareTo(value) > 0;
        });
        return this;
    }

    /**
     * 大于等于
     *
     * @param fun   属性
     * @param value 值
     */
    public <N extends Comparable<? super N>> Wrapper<T> ge(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        andPre(e -> {
            N v = Any.of(e).get(fun);
            if (Objects.isNull(v)) {
                return false;
            }
            return v.compareTo(value) >= 0;
        });
        return this;
    }

    /**
     * 区间
     *
     * @param fun        属性
     * @param leftValue  左值
     * @param rightValue 右值
     */
    public <N extends Comparable<? super N>> Wrapper<T> between(Function<? super T, ? extends N> fun, N leftValue, N rightValue) {
        if (EmptyUtil.isAllEmpty(leftValue, rightValue)) {
            return this;
        }
        if (Objects.nonNull(leftValue)) {
            and(p -> p.gt(fun, leftValue));
        }
        if (Objects.nonNull(rightValue)) {
            and(p -> p.le(fun, rightValue));
        }
        return this;
    }

    /**
     * 过滤指定字符属性以value开头的元素
     *
     * @param fun   属性
     * @param value 值
     */
    public Wrapper<T> rightLike(Function<? super T, String> fun, String value) {
        andPre(e -> {
            String str = Any.of(e).get(fun);
            if (EmptyUtil.isEmpty(str)) {
                return EmptyUtil.isEmpty(value);
            }
            if (EmptyUtil.isEmpty(value)) {
                return true;
            }
            return str.startsWith(value);
        });
        return this;
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     */
    public <V, C extends Collection<? extends V>> Wrapper<T> in(Function<? super T, ? extends V> fun, C values) {
        if (EmptyUtil.isEmpty(values)) {
            andPre(e -> false);
            return this;
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
    public <V> Wrapper<T> in(Function<? super T, ? extends V> fun, V... values) {
        if (EmptyUtil.isEmpty(values)) {
            andPre(e -> false);
            return this;
        }
        return in(fun, Stream.of(values).collect(Collectors.toSet()));
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    public <V, C extends Collection<? extends V>> Wrapper<T> notIn(Function<? super T, ? extends V> fun, C values) {
        if (EmptyUtil.isEmpty(values)) {
            return this;
        }
        filter(fun, v -> !values.contains(v));
        return this;
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     */
    @SuppressWarnings("unchecked")
    public <V> Wrapper<T> notIn(Function<? super T, ? extends V> fun, V... values) {
        return notIn(fun, new HashSet<>(Arrays.asList(values)));
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    public <C extends Collection<? extends T>> Wrapper<T> in(C c) {
        if (EmptyUtil.isEmpty(c)) {
            andPre(e -> false);
        } else {
            andPre(c::contains);
        }
        return this;
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     */
    @SuppressWarnings("all")
    public Wrapper<T> in(T... c) {
        return in(Stream.of(c).collect(Collectors.toSet()));
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param c c
     */
    public <C extends Collection<? extends T>> Wrapper<T> notIn(C c) {
        if (EmptyUtil.isEmpty(c)) {
            return this;
        }
        andPre(e -> !c.contains(e));
        return this;
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param values 指定值
     */
    @SuppressWarnings("all")
    public Wrapper<T> notIn(T... values) {
        return notIn(Stream.of(values).collect(Collectors.toSet()));
    }

    /**
     * 过滤某属性为空的元素
     *
     * @param fun 函数
     */
    public Wrapper<T> isNull(Function<? super T, ?> fun) {
        return filter(fun, Objects::isNull);
    }

    /**
     * 过滤为空的元素
     */
    public Wrapper<T> isNull() {
        andPre(Objects::isNull);
        return this;
    }

    /**
     * 过滤元素非空
     */
    public Wrapper<T> nonNull() {
        andPre(Objects::nonNull);
        return this;
    }

    /**
     * 过滤某属性不为空的元素
     *
     * @param fun 属性
     */
    public Wrapper<T> nonNull(Function<? super T, ?> fun) {
        return filter(fun, Objects::nonNull);
    }

    /**
     * 过滤某字符串属性为空的元素
     *
     * @param fun 属性
     */
    public Wrapper<T> isEmpty(Function<? super T, CharSequence> fun) {
        return filter(fun, EmptyUtil::isEmpty);
    }

    /**
     * 过滤某字符串属性不为空的元素
     *
     * @param fun 属性
     */
    public Wrapper<T> nonEmpty(Function<? super T, CharSequence> fun) {
        return filter(fun, EmptyUtil::isNotEmpty);
    }

    /**
     * 过滤某字符串属性为空白的元素
     *
     * @param fun 属性
     */
    public Wrapper<T> isBlank(Function<? super T, CharSequence> fun) {
        return filter(fun, StrValidator::isBlank);
    }

    /**
     * 过滤某字符串属性不为空白的元素
     *
     * @param fun 属性
     */
    public Wrapper<T> nonBlank(Function<? super T, CharSequence> fun) {
        return filter(fun, StrValidator::isNotBlank);
    }

    /**
     * 过滤
     *
     * @param fun       属性
     * @param predicate 判断函数
     */
    public <V> Wrapper<T> filter(Function<? super T, ? extends V> fun, Predicate<? super V> predicate) {
        andPre(e -> predicate.test(Any.of(e).get(fun)));
        return this;
    }
}

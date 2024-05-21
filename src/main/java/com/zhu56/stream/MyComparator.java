package com.zhu56.stream;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * 自定义比较器
 *
 * @author zhu56
 * @date 2024/05/22 00:56
 */
@UtilityClass
public class MyComparator {

    /**
     * 传入多个属性得到一个依次排序的排序器
     *
     * @param funs
     * @return {@link Comparator }<{@link T }>
     */
    public static <T, U extends Comparable<? super U>, F extends Function<? super T, ? extends U>> Comparator<T> multi(F... funs) {
        return Ztream.of(funs).reduce(
                null,
                (c, f) -> {
                    Function<F, Comparator<T>> method = Objects.isNull(c) ? Comparator::comparing : c::thenComparing;
                    return method.apply(f);
                },
                Comparator::thenComparing
        );
    }

    /**
     * 多个排序器组合为一个依次排序器
     *
     * @param comparators
     * @return {@link Comparator }<{@link T }>
     */
    public static <T, C extends Collection<Comparator<T>>> Comparator<T> multi(C comparators) {
        return Ztream.of(comparators).reduce(Comparator::thenComparing).orElse(null);
    }
}

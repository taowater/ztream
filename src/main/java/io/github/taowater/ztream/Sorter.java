package io.github.taowater.ztream;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * 流的排序操作
 *
 * @author zhu56
 * @date 2024/05/22 00:56
 */
public class Sorter<T> {

    /**
     * 默认排序不改变顺序
     */
    private Comparator<T> comparator = (o1, o2) -> 0;

    Comparator<T> getComparator() {
        return comparator;
    }

    /**
     * 排序
     *
     * @param keyExtractor 属性
     * @param desc         是否倒序
     * @param nullFirst    是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> sort(Function<? super T, ? extends U> keyExtractor, boolean desc, boolean nullFirst) {
        if (desc) {
            nullFirst = !nullFirst;
        }
        comparator = comparator.thenComparing(build(keyExtractor, nullFirst));
        if (desc) {
            comparator = comparator.reversed();
        }
        return this;
    }


    /**
     * 升序
     *
     * @param keyExtractor 属性
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> asc(Function<? super T, ? extends U> keyExtractor) {
        return sort(keyExtractor, false, true);
    }

    /**
     * 升序
     *
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> asc(Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return sort(keyExtractor, false, nullFirst);
    }

    /**
     * 降序
     *
     * @param keyExtractor 属性
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> desc(Function<? super T, ? extends U> keyExtractor) {
        return sort(keyExtractor, true, true);
    }

    /**
     * 降序
     *
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> desc(Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return sort(keyExtractor, true, nullFirst);
    }

    /**
     * 追加排序
     *
     * @param otherComparator 其他比较器
     * @param nullFirst       是否null值前置
     * @return {@link Sorter }<{@link T }>
     */
    public Sorter<T> then(Comparator<? super T> otherComparator, boolean nullFirst) {
        UnaryOperator<Comparator<? super T>> base = nullFirst ? Comparator::nullsFirst : Comparator::nullsLast;
        comparator = comparator.thenComparing(base.apply(otherComparator));
        return this;
    }

    /**
     * 追加排序
     *
     * @param keyExtractor  属性
     * @param keyComparator 排序器
     * @return {@link Sorter }<{@link T }>
     */
    public <U extends Comparable<? super U>> Sorter<T> then(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return then(Comparator.comparing(keyExtractor, keyComparator), true);
    }

    /**
     * 根据属性构建排序器
     *
     * @param keyExtractor 属性
     * @param nullFirst    是否null值前置
     * @return {@link Comparator }<{@link T }>
     */
    static <T, U extends Comparable<? super U>> Comparator<T> build(Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return (T o1, T o2) -> {
            U a = Any.of(o1).get(keyExtractor);
            U b = Any.of(o2).get(keyExtractor);
            if (a == null) {
                return (b == null) ? 0 : (nullFirst ? -1 : 1);
            }
            if (b == null) {
                return nullFirst ? 1 : -1;
            }
            return a.compareTo(b);
        };
    }

}

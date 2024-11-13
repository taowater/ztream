package com.taowater.ztream;


import com.taowater.ztream.assist.Box;
import com.taowater.ztream.assist.ExCollectors;
import com.taowater.ztream.op.Collect;
import com.taowater.ztream.op.Join;
import com.taowater.ztream.op.Judge;
import com.taowater.ztream.op.filter.Filter;
import com.taowater.ztream.op.math.Math;
import com.taowater.ztream.op.sort.Sort;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 抽象增强流
 *
 * @author Zhu56
 * @date 2022/11/13 21:37:12
 */
public abstract class AbstractZtream<T, S extends AbstractZtream<T, S>> implements Filter<T, S>, Sort<T, S>, Collect<T>,
        Math<T, S>,
        Join<T>,
        Judge<T, S> {
    protected final Stream<T> stream;

    protected AbstractZtream(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public Stream<T> stream() {
        return stream;
    }

    /**
     * 按某属性去重
     *
     * @param fun      属性
     * @param override 是否向前覆盖
     * @return {@link S }
     */
    public S distinct(Function<? super T, ?> fun, boolean override) {
        return ztream(handle(override, Sort::reverse).map(t -> {
            Object v = null;
            if (Objects.nonNull(t)) {
                v = fun.apply(t);
            }
            return new Box.PairBox<>(t, v);
        }).distinct().map(Box::getA));
    }

    /**
     * 去重
     *
     * @param fun 属性
     * @return {@link S }
     */
    public S distinct(Function<? super T, ?> fun) {
        return distinct(fun, true);
    }

    /**
     * 去重
     *
     * @param override 是否向前覆盖
     * @return {@link S }
     */
    public S distinct(boolean override) {
        return ztream(handle(override, Sort::reverse).distinct());
    }

    /**
     * 第一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> first() {
        return Any.of(findFirst(false).orElse(null));
    }

    /**
     * 获取第一个元素
     *
     * @return {@link T }
     */
    public T getFirst() {
        return first().orElse(null);
    }

    /**
     * 任意一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> any() {
        return Any.of(findAny(false).orElse(null));
    }

    /**
     * 最后一个
     *
     * @return {@link Any }<{@link T }>
     */
    public Any<T> last() {
        return collect(ExCollectors.last());
    }

    /**
     * 获取最后一个
     *
     * @return {@link T }
     */
    public T getLast() {
        return last().orElse(null);
    }

    /**
     * 随机取一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> random() {
        return shuffle().first();
    }
}

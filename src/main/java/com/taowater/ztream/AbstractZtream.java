package com.taowater.ztream;


import com.taowater.ztream.assist.Box;
import com.taowater.ztream.op.Collect;
import com.taowater.ztream.op.Join;
import com.taowater.ztream.op.filter.Filter;
import com.taowater.ztream.op.judge.Judge;
import com.taowater.ztream.op.math.Math;
import com.taowater.ztream.op.sort.Sort;

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
        return ztream(reverse(override)
                .map(t -> new Box.PairBox<>(t, Any.of(t).get(fun)))
                .distinct()
                .map(Box::getA))
                .reverse(override);
    }

    /**
     * 去重
     *
     * @param fun 属性
     * @return {@link S }
     */
    public S distinct(Function<? super T, ?> fun) {
        return distinct(fun, false);
    }

    /**
     * 去重
     *
     * @param override 是否向前覆盖
     * @return {@link S }
     */
    public S distinct(boolean override) {
        return reverse(override).distinct().reverse(override);
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
        return Any.of(stream.reduce((a, b) -> b).orElse(null));
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

package com.taowater.ztream;


import com.taowater.ztream.op.Collect;
import com.taowater.ztream.op.Join;
import com.taowater.ztream.op.ToEntry;
import com.taowater.ztream.op.filter.Filter;
import com.taowater.ztream.op.judge.Judge;
import com.taowater.ztream.op.math.Math;
import com.taowater.ztream.op.sort.Sort;

import java.util.stream.Stream;

/**
 * 抽象增强流
 *
 * @author Zhu56
 */
@SuppressWarnings("unused")
public abstract class AbstractZtream<T, S extends AbstractZtream<T, S>> implements Filter<T, S>, Sort<T, S>, Collect<T>,
        Math<T, S>,
        Join<T>,
        ToEntry<T, S>,
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

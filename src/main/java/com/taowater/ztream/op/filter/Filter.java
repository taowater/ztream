package com.taowater.ztream.op.filter;

import com.taowater.ztream.IZtream;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 过滤操作
 *
 * @author zhu56
 * @see 0.0.3
 */
public interface Filter<T, S extends IZtream<T, S>> extends IZtream<T, S>, Compare<T, S> {

    @Override
    default S filter(Predicate<? super T> predicate) {
        return IZtream.super.filter(predicate);
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

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

    @Override
    default S filter(boolean condition, Predicate<? super T> predicate) {
        if (condition) {
            return filter(predicate);
        }
        return ztream(this);
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
        return ztream(filter(e -> e, wrapper.getCondition()));
    }

    /**
     * 过滤条件为假
     *
     * @param predicate 谓语
     * @return {@link S }
     */
    default S isFalse(Predicate<? super T> predicate) {
        return filter(predicate.negate());
    }

    /**
     * 过滤条件为真
     *
     * @param predicate 谓语
     * @return {@link S }
     */
    default S isTrue(Predicate<? super T> predicate) {
        return filter(predicate);
    }
}

package com.taowater.ztream.op.filter;


import lombok.Getter;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 条件
 *
 * @author zhu56
 * @date 2024/09/16 01:43
 */
@Getter
public class Wrapper<T> implements Compare<T, Wrapper<T>> {

    private Predicate<T> condition = o -> true;

    @Override
    public Wrapper<T> filter(Predicate<? super T> predicate) {
        this.condition = condition.and(predicate);
        return this;
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
}

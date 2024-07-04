package com.taowater.ztream;

import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;

/**
 * 函数
 *
 * @author zhu56
 * @date 2024/07/04 23:50
 */
@UtilityClass
class Functions {

    /**
     * 索引消费者
     *
     * @author zhu56
     * @version 1.0
     * @date 2023/4/18 16:29
     */
    static class IndexedConsumer<T> implements Consumer<T> {
        private final AtomicInteger index = new AtomicInteger(0);

        private final ObjIntConsumer<? super T> consumer;

        public IndexedConsumer(ObjIntConsumer<T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) {
            consumer.accept(t, index.getAndAdd(1));
        }
    }

    /**
     * 索引功能
     *
     * @author zhu56
     * @version 1.0
     * @date 2023/05/11 00:03
     */
    static class IndexedFunction<T, R> implements Function<T, R> {
        private final AtomicInteger index = new AtomicInteger(0);

        private final BiFunction<T, Integer, R> fun;

        public IndexedFunction(BiFunction<T, Integer, R> fun) {
            this.fun = fun;
        }

        @Override
        public R apply(T t) {
            return fun.apply(t, index.getAndAdd(1));
        }
    }
}

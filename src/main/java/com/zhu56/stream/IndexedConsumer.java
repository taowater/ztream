package com.zhu56.stream;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

/**
 * 索引消费者
 *
 * @author zhu56
 * @version 1.0
 * @date 2023/4/18 16:29
 */
class IndexedConsumer<T> implements Consumer<T> {
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
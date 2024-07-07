package com.taowater.ztream;

import lombok.experimental.UtilityClass;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.Objects;
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

    /**
     * 构建一个键值对
     *
     * @param obj  元素
     * @param funK 键函数
     * @param funV 值函数
     * @return {@link Entry }<{@link K }, {@link V }>
     */
    static <T, K, V> Entry<K, V> entry(T obj, Function<? super T, ? extends K> funK, Function<? super T, ? extends V> funV) {
        K key = null;
        V value = null;
        if (Objects.nonNull(obj)) {
            key = funK.apply(obj);
            value = funV.apply(obj);
        }
        return new SimpleImmutableEntry<>(key, value);
    }

    /**
     * 构建一个新的键值对
     *
     * @param entry entry
     * @param funK  键函数
     * @param funV  值函数
     * @return {@link Entry }<{@link NK }, {@link NV }>
     */
    static <K, V, NK, NV> Entry<NK, NV> entryKeyValue(Entry<K, V> entry, Function<? super K, ? extends NK> funK, Function<? super V, ? extends NV> funV) {
        Function<Entry<K, V>, K> key = Entry::getKey;
        Function<Entry<K, V>, V> value = Entry::getValue;
        return entry(entry, key.andThen(funK), value.andThen(funV));
    }

    /**
     * 反转键值对
     *
     * @param entry entry
     * @return {@link Entry }<{@link V }, {@link K }>
     */
    static <K, V> Entry<V, K> flip(Entry<K, V> entry) {
        return entry(entry, Entry::getValue, Entry::getKey);
    }
}

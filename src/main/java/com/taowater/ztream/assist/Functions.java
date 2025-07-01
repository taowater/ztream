package com.taowater.ztream.assist;

import com.taowater.ztream.Any;
import lombok.experimental.UtilityClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

/**
 * 函数
 *
 * @author zhu56
 */
@UtilityClass
public class Functions {

    private final static Map<Class<?>, Predicate<?>> CACHE = new ConcurrentHashMap<>();

    /**
     * 索引消费者
     *
     * @author zhu56
     * @version 1.0
     */
    public static class IndexedConsumer<T> implements Consumer<T> {
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
     * 把一个返回布尔值的方法包装成一个拆箱安全的判断方法
     *
     * @param fun 方法
     */
    public static <T> Predicate<T> of(Function<T, Boolean> fun) {
        return e -> {
            Boolean result = fun.apply(e);
            if (Objects.isNull(result)) {
                return false;
            }
            return result;
        };
    }

    /**
     * 索引功能
     *
     * @author zhu56
     * @version 1.0
     */
    public static class IndexedFunction<T, R> implements Function<T, R> {
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
     * 将一个判断函数包装为空安全的判断函数
     * 注意缓存内存消耗
     *
     * @param predicate 谓语
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<? super T> safe(Predicate<? super T> predicate) {
        return (Predicate<? super T>) CACHE.computeIfAbsent(predicate.getClass(), k -> {
            try {
                MethodHandle mh = MethodHandles.lookup().unreflect(predicate.getClass()
                                .getMethod("test", Object.class))
                        .bindTo(predicate);
                return t -> {
                    if (t == null) {
                        return false;
                    }
                    try {
                        Boolean result = (Boolean) mh.invoke(t);
                        return Any.of(result).orElse(false);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                };
            } catch (Exception e) {
                return predicate;
            }
        });
    }

    /**
     * 构建一个键值对
     *
     * @param obj  元素
     * @param funK 键函数
     * @param funV 值函数
     */
    public static <T, K, V> Entry<K, V> entry(T obj, Function<? super T, ? extends K> funK, Function<? super T, ? extends V> funV) {
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
     */
    public static <K, V, NK, NV> Entry<NK, NV> entryKeyValue(Entry<K, V> entry, Function<? super K, ? extends NK> funK, Function<? super V, ? extends NV> funV) {
        Function<Entry<K, V>, K> key = Entry::getKey;
        Function<Entry<K, V>, V> value = Entry::getValue;
        return entry(entry, key.andThen(funK), value.andThen(funV));
    }

    /**
     * 反转键值对
     *
     * @param entry entry
     */
    public static <K, V> Entry<V, K> flip(Entry<K, V> entry) {
        return entry(entry, Entry::getValue, Entry::getKey);
    }
}

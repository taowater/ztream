package com.taowater.ztream;

import lombok.experimental.UtilityClass;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * 分割器相关
 *
 * @author zhu56
 * @date 2024/07/07 02:56
 */
@UtilityClass
class Spliterators {

    // 分组用分割器
    class GroupSpliterator<T, K, V, A, D> implements Spliterator<Entry<K, D>> {
        /**
         * 源分割器
         */
        private final Spliterator<T> sourceSpliterator;
        /**
         * 分族元素容器
         */
        private final Map<K, List<T>> groups = new HashMap<>();
        private Iterator<Entry<K, List<T>>> groupIterator;

        private final Function<? super T, ? extends K> funK;
        private final Function<? super T, ? extends V> funV;
        private final Collector<? super V, A, D> downstream;

        GroupSpliterator(Spliterator<T> sourceSpliterator,
                         Function<? super T, ? extends K> funK,
                         Function<? super T, ? extends V> funV,
                         Collector<? super V, A, D> downstream
        ) {
            this.sourceSpliterator = sourceSpliterator;
            this.funK = funK;
            this.funV = funV;
            this.downstream = downstream;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Entry<K, D>> action) {
            if (groupIterator == null) {
                sourceSpliterator.forEachRemaining(item -> {
                    K key = funK.apply(item);
                    groups.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
                });
                groupIterator = groups.entrySet().iterator();
            }
            if (groupIterator.hasNext()) {
                Map.Entry<K, List<T>> entry = groupIterator.next();
                System.out.println(123);
                action.accept(new SimpleImmutableEntry<>(entry.getKey(), Ztream.of(entry.getValue()).map(funV).collect(downstream)));
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<Entry<K, D>> trySplit() {
            return null; // 不支持并行拆分
        }

        @Override
        public long estimateSize() {
            return sourceSpliterator.estimateSize();
        }

        @Override
        public int characteristics() {
            return sourceSpliterator.characteristics() & ~(Spliterator.SIZED | Spliterator.SUBSIZED);
        }
    }
}

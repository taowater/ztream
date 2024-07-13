package com.taowater.ztream.assist;

import com.taowater.ztream.Ztream;
import lombok.experimental.UtilityClass;

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
public class Spliterators {

    // 分组用分割器
    public static class GroupSpliterator<T, K, V, A, D> implements Spliterator<Entry<K, D>> {
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

        public GroupSpliterator(Spliterator<T> sourceSpliterator,
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
                    K key = null;
                    if (Objects.nonNull(item)) {
                        key = funK.apply(item);
                    }
                    groups.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
                });
                groupIterator = groups.entrySet().iterator();
            }
            if (groupIterator.hasNext()) {
                Map.Entry<K, D> newEntry = Functions.entryKeyValue(groupIterator.next(), k -> k, v -> Ztream.of(v).map(i -> {
                    V iv = null;
                    if (Objects.nonNull(i)) {
                        iv = funV.apply(i);
                    }
                    return iv;
                }).collect(downstream));
                action.accept(newEntry);
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

    // 追加分割器
    public static class AppendSpliterator<T> implements Spliterator<T> {
        /**
         * 源分割器
         */
        private Spliterator<T> left;
        private Spliterator<T> right;
        private int characteristics;
        private long size;

        @SuppressWarnings("unchecked")
        public AppendSpliterator(Spliterator<? extends T> left,
                                 Spliterator<? extends T> right
        ) {

            this.left = (Spliterator<T>) left;
            this.right = (Spliterator<T>) right;
            this.characteristics = left.characteristics() & right.characteristics() & (ORDERED | SIZED | SUBSIZED);
            this.size = left.estimateSize() + right.estimateSize();
            if (this.size < 0) {
                this.size = Long.MAX_VALUE;
                this.characteristics &= (~SIZED) & (~SUBSIZED);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (left != null) {
                if (left.tryAdvance(action)) {
                    if (size > 0 && size != Long.MAX_VALUE) {
                        size--;
                    }
                    return true;
                }
                left = null;
            }
            return right.tryAdvance(action);
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {

            if (left != null) {
                left.forEachRemaining(action);
            }
            if (right != null) {
                right.forEachRemaining(action);
            }
        }

        @Override
        public Spliterator<T> trySplit() {
            if (left == null) {
                return right.trySplit();
            }
            Spliterator<T> s = left;
            left = null;
            return s;
        }

        @Override
        public long estimateSize() {
            if (left == null) {
                return right == null ? 0 : right.estimateSize();
            }
            return size;
        }

        @Override
        public int characteristics() {
            return characteristics;
        }
    }
}

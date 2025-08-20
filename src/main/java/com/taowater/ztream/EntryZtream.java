package com.taowater.ztream;

import com.taowater.ztream.assist.Functions;
import lombok.var;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * 键值对ztream
 *
 * @author zhu56
 */
@SuppressWarnings({"unused", "unchecked"})
public final class EntryZtream<K, V> extends AbstractZtream<Entry<K, V>, EntryZtream<K, V>> {

    @SuppressWarnings("unchecked")
    EntryZtream(Stream<? extends Entry<K, V>> stream) {
        super((Stream<Entry<K, V>>) stream);
    }

    public static <K, V> EntryZtream<K, V> empty() {
        return new EntryZtream<>(Stream.empty());
    }

    public static <K, V, C extends Collection<Entry<K, V>>> EntryZtream<K, V> of(C collection) {
        return new EntryZtream<>(Ztream.of(collection));
    }

    public static <K, V, S extends Stream<? extends Entry<K, V>>> EntryZtream<K, V> of(S stream) {
        return new EntryZtream<>(stream);
    }

    @Override
    public EntryZtream<K, V> ztream(Stream<Entry<K, V>> stream) {
        return new EntryZtream<>(stream);
    }

    @Override
    public <R> Ztream<R> map(Function<? super Entry<K, V>, ? extends R> mapper) {
        return Ztream.of(stream.map(mapper));
    }

    @Override
    public <R> Ztream<R> flatMap(Function<? super Entry<K, V>, ? extends Stream<? extends R>> mapper) {
        return Ztream.of(stream.flatMap(mapper));
    }

    public Ztream<K> keys() {
        return map(Entry::getKey);
    }

    public Ztream<V> values() {
        return map(Entry::getValue);
    }

    public Map<K, V> toMap() {
        return toMap(HashMap::new);
    }

    public Map<K, V> toMap(Supplier<? extends Map<K, V>> supplier) {
        var map = supplier.get();
        return reduce(map,
                (m, e) -> {
                    m.put(e.getKey(), e.getValue());
                    return m;
                },
                (m1, m2) -> {
                    m1.putAll(m2);
                    return m1;
                });
    }

    public void forEachKeyValue(BiConsumer<K, V> consumer) {
        forEach(e -> consumer.accept(e.getKey(), e.getValue()));
    }

    public void forEachKey(Consumer<K> consumer) {
        forEach(e -> consumer.accept(e.getKey()));
    }

    public void forEachValue(Consumer<V> consumer) {
        forEach(e -> consumer.accept(e.getValue()));
    }

    public EntryZtream<K, V> peekKeyValue(BiConsumer<K, V> consumer) {
        return peek(e -> consumer.accept(e.getKey(), e.getValue()));
    }

    public EntryZtream<K, V> peekKey(Consumer<K> consumer) {
        return peek(e -> consumer.accept(e.getKey()));
    }

    public EntryZtream<K, V> peekValue(Consumer<V> consumer) {
        return peek(e -> consumer.accept(e.getValue()));
    }

    public EntryZtream<K, V> nonNullKey() {
        return filter(e -> e.getKey() != null);
    }

    public EntryZtream<K, V> nonNullValue() {
        return filter(e -> e.getValue() != null);
    }

    public EntryZtream<K, V> filter(BiPredicate<? super K, ? super V> predicate) {
        return filter(e -> predicate.test(e.getKey(), e.getValue()));
    }


    public EntryZtream<K, V> filterKey(Predicate<? super K> predicate) {
        return filter(e -> predicate.test(e.getKey()));
    }

    public EntryZtream<K, V> filterValue(Predicate<? super V> predicate) {
        return filter(e -> predicate.test(e.getValue()));
    }

    public boolean anyMatch(BiPredicate<? super K, ? super V> predicate) {
        return anyMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    public boolean allMatch(BiPredicate<? super K, ? super V> predicate) {
        return allMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    public boolean noneMatch(BiPredicate<? super K, ? super V> predicate) {
        return noneMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    public <N> EntryZtream<N, V> mapKey(Function<? super K, ? extends N> funK) {
        return (EntryZtream<N, V>) new EntryZtream<>(map(e -> Functions.entryKeyValue(e, funK, Function.identity()))).distinctKey();
    }

    public <N> EntryZtream<K, N> mapValue(Function<? super V, ? extends N> funV) {
        return new EntryZtream<>(map(e -> Functions.entryKeyValue(e, Function.identity(), funV)));
    }


    public <R> Ztream<R> mapKeyValue(BiFunction<? super K, ? super V, ? extends R> fun) {
        return map(e -> fun.apply(e.getKey(), e.getValue()));
    }

    public EntryZtream<V, K> flip() {
        return new EntryZtream<>(map(Functions::flip));
    }


    public EntryZtream<K, V> distinctKey() {
        return distinct(Entry::getKey);
    }

    public EntryZtream<K, V> distinctValue() {
        return distinct(Entry::getValue);
    }
}

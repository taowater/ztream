package com.taowater.ztream;

import lombok.var;

import java.util.AbstractMap.SimpleImmutableEntry;
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
 * @date 2024/07/04 21:49
 */
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

    public static <K, V> EntryZtream<K, V> of(Ztream<? extends Entry<K, V>> stream) {
        return new EntryZtream<>(stream);
    }

    public static <K, V> EntryZtream<K, V> of(Stream<? extends Entry<K, V>> stream) {
        return new EntryZtream<>(stream);
    }

    @Override
    protected EntryZtream<K, V> wrap(Stream<Entry<K, V>> stream) {
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

    public void forEach(BiConsumer<K, V> consumer) {
        forEach(e -> consumer.accept(e.getKey(), e.getValue()));
    }

    public void forEachKey(Consumer<K> consumer) {
        forEach(e -> consumer.accept(e.getKey()));
    }

    public void forEachValue(Consumer<V> consumer) {
        forEach(e -> consumer.accept(e.getValue()));
    }

    public EntryZtream<K, V> peek(BiConsumer<K, V> consumer) {
        return peek(e -> consumer.accept(e.getKey(), e.getValue()));
    }

    public EntryZtream<K, V> peekKey(Consumer<K> consumer) {
        return peek(e -> consumer.accept(e.getKey()));
    }

    public EntryZtream<K, V> peekValue(Consumer<V> consumer) {
        return peek(e -> consumer.accept(e.getValue()));
    }

    public EntryZtream<K, V> nonNullKeys() {
        return filter(e -> e.getKey() != null);
    }

    public EntryZtream<K, V> nonNullValues() {
        return filter(e -> e.getValue() != null);
    }

    public EntryZtream<K, V> filter(BiPredicate<? super K, ? super V> predicate) {
        return filter(e -> predicate.test(e.getKey(), e.getValue()));
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

    public <NK> EntryZtream<NK, V> mapKey(BiFunction<? super K, ? super V, ? extends NK> funK) {
        return new EntryZtream<>(map(e -> new SimpleImmutableEntry<>(funK.apply(e.getKey(), e.getValue()), e.getValue())));
    }

    public <NV> EntryZtream<K, NV> mapValue(BiFunction<? super K, ? super V, ? extends NV> funV) {
        return new EntryZtream<>(map(e -> new SimpleImmutableEntry<>(e.getKey(), funV.apply(e.getKey(), e.getValue()))));
    }

    public <R> Ztream<R> map(BiFunction<? super K, ? super V, ? extends R> fun) {
        return map(e -> fun.apply(e.getKey(), e.getValue()));
    }

    public EntryZtream<K, V> distinctKeys() {
        return distinct(Entry::getKey);
    }

    public EntryZtream<K, V> distinctValues() {
        return distinct(Entry::getValue);
    }
}

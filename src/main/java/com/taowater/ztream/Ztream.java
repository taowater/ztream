package com.taowater.ztream;

import com.taowater.taol.core.convert.ConvertUtil;
import com.taowater.taol.core.function.Function2;
import com.taowater.taol.core.util.EmptyUtil;
import com.taowater.ztream.assist.BreakException;
import com.taowater.ztream.assist.Functions;
import com.taowater.ztream.assist.Spliterators;
import com.taowater.ztream.op.GroupBy;
import com.taowater.ztream.op.ToMap;
import lombok.var;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 增强流
 *
 * @author Zhu56
 */
@SuppressWarnings({"unused", "unchecked"})
public final class Ztream<T> extends AbstractZtream<T, Ztream<T>> implements GroupBy<T>,
        ToMap<T> {

    Ztream(Stream<T> stream) {
        super(stream);
    }

    @Override
    public Ztream<T> ztream(Stream<T> stream) {
        return new Ztream<>(stream);
    }

    @Override
    public <R> Ztream<R> map(Function<? super T, ? extends R> mapper) {
        return new Ztream<>(stream.map(mapper));
    }

    /**
     * 带索引的元素映射
     *
     * @param mapper 映射器
     * @return {@link Ztream}<{@link R}>
     */
    public <R> Ztream<R> map(BiFunction<? super T, Integer, ? extends R> mapper) {
        return new Ztream<>(stream.map(new Functions.IndexedFunction<>(mapper)));
    }

    @Override
    public <R> Ztream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new Ztream<>(stream.flatMap(mapper));
    }

    /**
     * 返回一个空的串行流
     *
     * @return {@link Ztream}<{@link T}>
     */
    public static <T> Ztream<T> empty() {
        return new Ztream<>(Stream.empty());
    }

    public static <K, V> EntryZtream<K, V> of(Map<K, V> map) {
        return EmptyUtil.isEmpty(map) ? EntryZtream.empty() : EntryZtream.of(map.entrySet());
    }

    /**
     * 不定量元素创建流
     *
     * @param values 若干元素
     * @return {@link Ztream}<{@link T}>
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Ztream<T> of(T... values) {
        return EmptyUtil.isEmpty(values) ? empty() : of(Stream.of(values));
    }

    /**
     * 由实现{@link Iterable}接口的对象创建非并行流
     *
     * @param iterable iterable
     * @return {@link Ztream}<{@link T}>
     */
    public static <T> Ztream<T> of(Iterable<T> iterable) {
        return of(iterable, false);
    }

    /**
     * 由实现{@link Iterable}接口的对象创建流
     *
     * @param iterable iterable
     * @param parallel 是否并行
     * @return {@link Ztream}<{@link T}>
     */
    public static <T> Ztream<T> of(Iterable<T> iterable, boolean parallel) {
        return Any.of(iterable).map(Iterable::spliterator).map(spliterator -> StreamSupport.stream(spliterator, parallel)).map(Ztream::new).orElseGet(Ztream::empty);
    }

    /**
     * 从标准流创建增强流
     *
     * @param stream 流
     * @return {@link Ztream}<{@link T}>
     */
    public static <T> Ztream<T> of(Stream<T> stream) {
        return Any.of(stream).map(Ztream::new).orElseGet(Ztream::empty);
    }

    /**
     * 遍历
     *
     * @param action 行动
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> peek(ObjIntConsumer<? super T> action) {
        return peek(new Functions.IndexedConsumer<>(action));
    }

    /**
     * 遍历
     *
     * @param action 当前元素及遍历下标
     */
    public void forEach(ObjIntConsumer<? super T> action) {
        forEach(new Functions.IndexedConsumer<>(action));
    }

    /**
     * 转换类型
     *
     * @param clazz clazz
     */
    public <N> Ztream<N> convert(Class<N> clazz) {
        return convert(clazz, ConvertUtil::convert, null);
    }

    /**
     * 转换元素类型
     *
     * @param clazz      类型
     * @param convertFun 转换方法
     */
    public <N> Ztream<N> convert(Class<N> clazz, Function2<T, Class<N>, N> convertFun) {
        return convert(clazz, convertFun, null);
    }

    /**
     * 转换元素类型，按新旧元素入参的消费方法处理元素
     *
     * @param clazz    clazz
     * @param consumer 处理函数
     */
    public <N> Ztream<N> convert(Class<N> clazz, BiConsumer<T, N> consumer) {
        return convert(clazz, ConvertUtil::convert, consumer);
    }

    /**
     * 转换
     *
     * @param clazz      转换类型
     * @param convertFun 转换方法
     * @param consumer   新旧对象的上下文处理函数
     */
    public <N> Ztream<N> convert(Class<N> clazz, Function2<T, Class<N>, N> convertFun, BiConsumer<T, N> consumer) {
        return map(e -> {
            N n = convertFun.apply(e, clazz);
            if (Objects.nonNull(consumer)) {
                consumer.accept(e, n);
            }
            return n;
        });
    }

    /**
     * 追加元素
     *
     * @param values 值
     */
    @SafeVarargs
    public final Ztream<T> append(T... values) {
        if (EmptyUtil.isEmpty(values)) {
            return this;
        }
        return append(Arrays.spliterator(values));
    }

    /**
     * 追加元素
     *
     * @param iterable iterable 可迭代元素
     */
    public Ztream<T> append(Iterable<? extends T> iterable) {
        if (Objects.isNull(iterable)) {
            return this;
        }
        return append(iterable.spliterator());
    }

    /**
     * 追加元素
     *
     * @param spliterator 分割器
     */
    public Ztream<T> append(Spliterator<? extends T> spliterator) {
        if (EmptyUtil.isEmpty(spliterator)) {
            return this;
        }
        Spliterator<T> left = spliterator();
        Spliterator<T> result = (Spliterator<T>) spliterator;
        if (left.getExactSizeIfKnown() != 0) {
            result = new Spliterators.AppendSpliterator<>(left, spliterator);
        }
        return Ztream.of(StreamSupport.stream(result, isParallel()));
    }

    /**
     * 找到符合条件的第一个元素的下标
     *
     * @param predicate 判断函数
     * @return int 下标
     */
    public int firstIdx(Predicate<T> predicate) {
        AtomicInteger index = new AtomicInteger(-1);
        try {
            this.forEach((e, i) -> {
                if (predicate.test(e)) {
                    index.set(i);
                    throw new BreakException();
                }
            });
        } catch (BreakException ignore) {
        }
        return index.get();
    }

    /**
     * 收集某个集合类型的属性并展开
     *
     * @param mapper 属性
     */
    public <N, C extends Collection<N>> Ztream<N> flat(Function<? super T, ? extends C> mapper) {
        return this.map(mapper).flatMap(Ztream::of);
    }

    /**
     * 强转元素类型
     *
     * @param clazz 目标类型
     */
    public <N> Ztream<N> cast(Class<N> clazz) {
        return map(clazz::cast);
    }

    /**
     * 分页
     *
     * @param no   页码
     * @param size 页长
     */
    public Ztream<T> page(long no, long size) {
        return skip((no - 1) * size).limit(size);
    }

    /**
     * 映射
     *
     * @param funK 键函数
     * @return {@link EntryZtream }<{@link K }, {@link T }>
     */
    public <K> EntryZtream<K, T> hash(Function<? super T, K> funK) {
        return hash(funK, Function.identity());
    }

    /**
     * 映射
     *
     * @param funK     键函数
     * @param funV     值函数
     * @param override 是否向前覆盖
     * @return {@link EntryZtream }<{@link K }, {@link V }>
     */
    public <K, V> EntryZtream<K, V> hash(Function<? super T, K> funK, Function<? super T, V> funV, boolean override) {
        return EntryZtream.of(map(e -> Functions.entry(e, funK, funV)).distinct(Map.Entry::getKey, override));
    }

    /**
     * 映射-重复会向前覆盖
     *
     * @param funK 键函数
     * @param funV 值函数
     * @return {@link EntryZtream }<{@link K }, {@link V }>
     */
    public <K, V> EntryZtream<K, V> hash(Function<? super T, K> funK, Function<? super T, V> funV) {
        return hash(funK, funV, true);
    }

    /**
     * 分组
     *
     * @param funK       键函数
     * @param funV       值函数
     * @param downstream 组元素处理
     * @return {@link EntryZtream }<{@link K }, {@link D }>
     */
    public <K, V, A, D> EntryZtream<K, D> group(Function<? super T, K> funK, Function<? super T, V> funV, Collector<? super V, A, D> downstream) {
        var spliterator = new Spliterators.GroupSpliterator<>(spliterator(), funK, funV, downstream);
        return EntryZtream.of(StreamSupport.stream(spliterator, isParallel()));
    }

    /**
     * 分组
     *
     * @param funK 键函数
     * @param funV 值函数
     * @return {@link EntryZtream }<{@link K }, {@link List }<{@link V }>>
     */
    public <K, V> EntryZtream<K, List<V>> group(Function<? super T, K> funK, Function<? super T, V> funV) {
        return group(funK, funV, Collectors.toList());
    }

    /**
     * 分组
     *
     * @param funK 键函数
     * @return {@link EntryZtream }<{@link K }, {@link List }<{@link T }>>
     */
    public <K> EntryZtream<K, List<T>> group(Function<? super T, K> funK) {
        return group(funK, Function.identity());
    }
}

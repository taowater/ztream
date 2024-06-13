package io.github.taowater.ztream;


import io.github.taowater.function.IndexedConsumer;
import io.github.taowater.function.IndexedFunction;
import io.github.taowater.util.ConvertUtil;
import io.github.taowater.util.EmptyUtil;
import lombok.var;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 增强流
 *
 * @author Zhu56
 * @date 2022/11/13 01:11:56
 */
@SuppressWarnings("unchecked")
public final class Ztream<T> extends AbstractZtream<T, Ztream<T>> implements Collect<T>,
        GroupBy<T>,
        ToMap<T>,
        Math<T>,
        Join<T> {

    Ztream(Stream<T> stream) {
        super(stream);
    }

    @Override
    protected Ztream<T> wrap(Stream<T> stream) {
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
        return new Ztream<>(stream.map(new IndexedFunction<>(mapper)));
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
     * 升序
     *
     * @param fun 属性
     * @return {@link Ztream}<{@link T}>
     */
    public <U extends Comparable<? super U>> Ztream<T> asc(Function<? super T, ? extends U> fun) {
        return asc(fun, true);
    }

    /**
     * 升序
     *
     * @param fun       属性
     * @param nullFirst 是否null值前置
     * @return {@link Ztream }<{@link T }>
     */
    public <U extends Comparable<? super U>> Ztream<T> asc(Function<? super T, ? extends U> fun, boolean nullFirst) {
        return sort(r -> r.asc(fun, nullFirst));
    }

    /**
     * 升序
     *
     * @return {@link Ztream }<{@link T }>
     */
    public Ztream<T> asc() {
        return asc(true);
    }

    /**
     * 升序
     *
     * @param nullFirst 是否null值前置
     * @return {@link Ztream }<{@link T }>
     */
    public Ztream<T> asc(boolean nullFirst) {
        return sort(r -> r.then((Comparator<T>) Comparator.naturalOrder(), nullFirst));
    }

    /**
     * 降序
     *
     * @return {@link Ztream}<{@link T}>
     */
    public <U extends Comparable<? super U>> Ztream<T> desc(Function<? super T, ? extends U> fun) {
        return desc(fun, true);
    }

    /**
     * 降序
     *
     * @param fun       属性
     * @param nullFirst 是否null值前置
     * @return {@link Ztream }<{@link T }>
     */
    public <U extends Comparable<? super U>> Ztream<T> desc(Function<? super T, ? extends U> fun, boolean nullFirst) {
        return sort(r -> r.desc(fun, nullFirst));
    }

    /**
     * 降序
     *
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> desc() {
        return desc(true);
    }

    /**
     * 降序
     *
     * @param nullFirst 是否null值前置
     * @return {@link Ztream }<{@link T }>
     */
    public Ztream<T> desc(boolean nullFirst) {
        return sort(r -> r.then((Comparator<T>) Comparator.reverseOrder(), nullFirst));
    }

    /**
     * 排序
     *
     * @param consumer 排序上下分的消费函数
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> sort(Consumer<Sorter<T>> consumer) {
        Sorter<T> sorter = new Sorter<>();
        consumer.accept(sorter);
        return sorted(sorter.getComparator());
    }

    /**
     * 按某元素去重
     *
     * @param fun 函数
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> distinct(Function<? super T, ?> fun) {
        return distinct(fun, true);
    }

    /**
     * 按某元素去重
     *
     * @param fun      属性
     * @param override 是否向前覆盖
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> distinct(Function<? super T, ?> fun, boolean override) {
        return collect(ExCollectors.distinct(fun, override));
    }

    /**
     * 第一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> first() {
        return Any.of(findFirst().orElse(null));
    }

    /**
     * 任意一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> any() {
        return Any.of(findAny().orElse(null));
    }

    /**
     * 如果流集合不为空，则执行以所有元素收集成List为入参的消费函数
     *
     * @param consumer 消费者
     */
    public <C extends Collection<T>> void ifNotEmpty(Consumer<C> consumer) {
        C list = (C) this.toList();
        if (EmptyUtil.isNotEmpty(list)) {
            consumer.accept(list);
        }
    }

    /**
     * 遍历
     *
     * @param action 行动
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> peek(ObjIntConsumer<? super T> action) {
        return peek(new IndexedConsumer<>(action));
    }

    /**
     * 遍历
     *
     * @param action 当前元素及遍历下标
     */
    public void forEach(ObjIntConsumer<? super T> action) {
        forEach(new IndexedConsumer<>(action));
    }

    /**
     * 判断元素是否有重复
     *
     * @return boolean
     */
    public boolean hadRepeat() {
        Set<T> set = new HashSet<>();
        return anyMatch(x -> !set.add(x));
    }

    /**
     * 判断元素某个属性是有重复
     *
     * @param function 函数
     * @return boolean
     */
    public boolean hadRepeat(Function<? super T, ?> function) {
        return map(function).hadRepeat();
    }

    /**
     * 转换
     *
     * @param clazz clazz
     * @return {@link Ztream}<{@link N}>
     */
    public <N> Ztream<N> convert(Class<N> clazz) {
        return map(e -> ConvertUtil.convert(e, clazz));
    }

    /**
     * 转换元素类型，按新旧元素入参的消费方法处理元素
     *
     * @param clazz    clazz
     * @param consumer 消费者
     * @return {@link Ztream}<{@link N}>
     */
    public <N> Ztream<N> convert(Class<N> clazz, BiConsumer<T, N> consumer) {
        return map(e -> {
            N n = ConvertUtil.convert(e, clazz);
            consumer.accept(e, n);
            return n;
        });
    }

    /**
     * 追加元素
     *
     * @param values 值
     * @return {@link Ztream}<{@link T}>
     */
    @SafeVarargs
    public final Ztream<T> append(T... values) {
        return this.append(Ztream.of(values).toList());
    }

    /**
     * 追加元素
     *
     * @param iterable iterable
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> append(Iterable<? extends T> iterable) {
        List<T> list = this.toList();
        if (EmptyUtil.isNotEmpty(iterable)) {
            list.addAll(Ztream.of(iterable).toList());
        }
        return Ztream.of(list);
    }

    /**
     * 对元素进行洗牌
     *
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> shuffle() {
        List<T> list = toList();
        Collections.shuffle(list);
        return of(list);
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
     * @param mapper
     * @return {@link Ztream}<{@link N}>
     */
    public <N, C extends Collection<N>> Ztream<N> flat(Function<? super T, ? extends C> mapper) {
        return this.map(mapper).flatMap(Ztream::of);
    }

    /**
     * 强转元素类型
     *
     * @param clazz 目标类型
     * @return {@link Ztream}<{@link N}>
     */
    public <N> Ztream<N> cast(Class<N> clazz) {
        return map(clazz::cast);
    }

    /**
     * 随机取一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> randomOne() {
        return shuffle().first();
    }

    /**
     * 等值操作
     *
     * @param fun   属性
     * @param value 值
     * @return {@link Ztream}<{@link T}>
     */
    public <V> Ztream<T> eq(Function<? super T, ? extends V> fun, V value) {
        return Objects.isNull(value)
                ? filter(e -> Any.of(e).map(fun).isEmpty())
                : this.filter(e -> Objects.equals(fun.apply(e), value));
    }

    /**
     * 小于
     *
     * @param fun   属性
     * @param value 值
     * @return {@link Ztream }<{@link T }>
     */
    public <N extends Comparable<? super N>> Ztream<T> lt(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return this.nonNull(fun).filter(e -> fun.apply(e).compareTo(value) < 0);
    }

    /**
     * 小于等于
     *
     * @param fun   属性
     * @param value 值
     * @return {@link Ztream }<{@link T }>
     */
    public <N extends Comparable<? super N>> Ztream<T> le(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return this.nonNull(fun).filter(e -> fun.apply(e).compareTo(value) <= 0);
    }

    /**
     * 大于
     *
     * @param fun   属性
     * @param value 值
     * @return {@link Ztream }<{@link T }>
     */
    public <N extends Comparable<? super N>> Ztream<T> gt(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return this.nonNull(fun).filter(e -> fun.apply(e).compareTo(value) > 0);
    }

    /**
     * 大于等于
     *
     * @param fun   属性
     * @param value 值
     * @return {@link Ztream }<{@link T }>
     */
    public <N extends Comparable<? super N>> Ztream<T> ge(Function<? super T, ? extends N> fun, N value) {
        Objects.requireNonNull(value);
        return this.nonNull(fun).filter(e -> fun.apply(e).compareTo(value) >= 0);
    }

    /**
     * 区间
     *
     * @param fun        属性
     * @param leftValue  左值
     * @param rightValue 右值
     * @return {@link Ztream }<{@link T }>
     */
    public <N extends Comparable<? super N>> Ztream<T> between(Function<? super T, ? extends N> fun, N leftValue, N rightValue) {
        if (EmptyUtil.isAllEmpty(leftValue, rightValue)) {
            return this;
        }
        var s = this.nonNull(fun);
        if (Objects.nonNull(leftValue)) {
            s = s.ge(fun, leftValue);
        }
        if (Objects.nonNull(rightValue)) {
            s = s.le(fun, rightValue);
        }
        return s;
    }

    /**
     * 过滤指定字符属性以value开头的元素
     *
     * @param fun   属性
     * @param value 值
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> rightLike(Function<? super T, String> fun, String value) {
        return this.filter(e -> {
            String str = fun.apply(e);
            if (EmptyUtil.isEmpty(str)) {
                return EmptyUtil.isEmpty(value);
            }
            if (EmptyUtil.isEmpty(value)) {
                return true;
            }
            return str.startsWith(value);
        });
    }

    /**
     * 不等操作
     *
     * @param fun   属性
     * @param value 值
     * @return {@link Ztream}<{@link T}>
     */
    public <V> Ztream<T> ne(Function<? super T, ? extends V> fun, V value) {
        return Objects.isNull(value)
                ? filter(e -> Any.of(e).map(fun).isPresent())
                : this.filter(e -> !Objects.equals(fun.apply(e), value));
    }

    /**
     * 不等操作
     *
     * @param value 值
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> ne(T value) {
        return this.filter(e -> !Objects.equals(e, value));
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     * @return {@link Ztream}<{@link T}>
     */
    public <V> Ztream<T> in(Function<? super T, ? extends V> fun, Collection<? extends V> values) {
        return EmptyUtil.isEmpty(values) ? Ztream.empty() : this.filter(e -> values.contains(fun.apply(e)));
    }

    /**
     * in操作
     *
     * @param fun    属性
     * @param values 值
     * @return {@link Ztream}<{@link T}>
     */
    public <V> Ztream<T> in(Function<? super T, ? extends V> fun, V... values) {
        return in(fun, Ztream.of(values).toList());
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     * @return {@link Ztream}<{@link T}>
     */
    public <V> Ztream<T> notIn(Function<? super T, ? extends V> fun, Collection<? extends V> values) {
        return EmptyUtil.isEmpty(values) ? this : this.filter(e -> !values.contains(fun.apply(e)));
    }

    /**
     * notin操作
     *
     * @param fun    属性
     * @param values 值
     * @return {@link Ztream}<{@link T}>
     */
    public <V> Ztream<T> notIn(Function<? super T, ? extends V> fun, V... values) {
        return notIn(fun, Ztream.of(values).toList());
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> in(Collection<T> c) {
        return EmptyUtil.isEmpty(c) ? Ztream.empty() : this.filter(c::contains);
    }

    /**
     * 过滤在指定集合中的元素
     *
     * @param c c
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> in(T... c) {
        return in(Ztream.of(c).toSet());
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param c c
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> notIn(Collection<T> c) {
        return EmptyUtil.isEmpty(c) ? this : this.filter(e -> !c.contains(e));
    }

    /**
     * 过滤不在指定集合中的元素
     *
     * @param c c
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> notIn(T... c) {
        return notIn(Ztream.of(c).toSet());
    }

    /**
     * 过滤某属性为空的元素
     *
     * @param fun 函数
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> isNull(Function<? super T, ?> fun) {
        return this.filter(e -> Any.of(e).map(fun).isEmpty());
    }

    /**
     * 过滤某属性不为空的元素
     *
     * @param fun 属性
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> nonNull(Function<? super T, ?> fun) {
        return this.filter(e -> Any.of(e).map(fun).isPresent());
    }

    /**
     * 过滤元素非空
     *
     * @return {@link Ztream}<{@link T}>
     */
    public Ztream<T> nonNull() {
        return this.filter(Objects::nonNull);
    }
}

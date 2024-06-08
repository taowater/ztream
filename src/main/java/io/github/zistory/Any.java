package io.github.zistory;

import io.github.zistory.util.ConvertUtil;
import io.github.zistory.util.EmptyUtil;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 自定义Optional
 *
 * @author Zhu56
 * @date 2023/03/15 23:19:46
 */
@SuppressWarnings("unchecked")
public final class Any<T> {

    private static final Any<?> EMPTY = new Any<>(null);

    private final T value;

    public static <T> Any<T> empty() {
        @SuppressWarnings("unchecked")
        Any<T> t = (Any<T>) EMPTY;
        return t;
    }

    private Any(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static <T> Any<T> of(T value) {
        return Objects.isNull(value) ? (Any<T>) EMPTY : new Any<>(value);
    }

    @SuppressWarnings("unchecked")
    public static Any<String> of(String value) {
        return EmptyUtil.isEmpty(value) ? (Any<String>) EMPTY : new Any<>(value);
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public <U> U get(Function<? super T, ? extends U> mapper, U defaultValue) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return defaultValue;
        }
        return mapper.apply(value);
    }

    public <U> U get(Function<? super T, ? extends U> mapper) {
        return this.get(mapper, null);
    }


    public boolean isPresent() {
        return EmptyUtil.isNotEmpty(value);
    }


    public boolean isEmpty() {
        return EmptyUtil.isEmpty(value);
    }


    @SuppressWarnings("all")
    public Any<T> ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
        return this;
    }

    @SafeVarargs
    public final <V> Any<T> ifPresent(Function<T, V> function, Consumer<V>... actions) {
        V tempValue = function.apply(value);
        if (Objects.isNull(tempValue)) {
            return this;
        }
        Ztream.of(actions).forEach(action -> action.accept(tempValue));
        return this;
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (value != null) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    public Any<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        }
        return predicate.test(value) ? this : empty();
    }

    public <U> Any<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (Objects.isNull(value)) {
            return empty();
        }
        return Any.of(mapper.apply(value));
    }

    public <U> Any<U> flatMap(Function<? super T, Any<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        }
        @SuppressWarnings("unchecked")
        Any<U> r = (Any<U>) mapper.apply(value);
        return Objects.requireNonNull(r);
    }

    /**
     * 收集某个集合类型的属性并展开为流
     *
     * @param mapper 属性
     * @return {@link Ztream}<{@link N}> 结果
     */
    public <N, C extends Collection<N>> Ztream<N> ztream(Function<T, C> mapper) {
        Objects.requireNonNull(mapper);
        return Ztream.of(this.get(mapper));
    }

    public Any<T> or(Supplier<Any<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isPresent()) {
            return this;
        }
        @SuppressWarnings("unchecked")
        Any<T> r = (Any<T>) supplier.get();
        return Objects.requireNonNull(r);
    }

    public Ztream<T> ztream() {
        return !isPresent() ? Ztream.empty() : Ztream.of(value);
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }

    public T orElseThrow() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        }
        throw exceptionSupplier.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Any)) {
            return false;
        }

        Any<?> other = (Any<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return isPresent() ? value.toString() : null;
    }

    /**
     * 转换类型
     *
     * @param clazz clazz
     * @return {@link Any}<{@link N}>
     */
    public <N> Any<N> convert(Class<N> clazz) {
        return this.map(e -> ConvertUtil.convert(e, clazz));
    }

    /**
     * 获取转换类型后的对象
     *
     * @param clazz clazz
     * @return {@link N}
     */
    public <N> N get(Class<N> clazz) {
        return this.convert(clazz).orElse(null);
    }

    /**
     * 偷看
     *
     * @param consumer 消费者
     * @return {@link Any}<{@link T}>
     */
    public Any<T> peek(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);
        if (Objects.nonNull(value)) {
            consumer.accept(value);
        }
        return this;
    }
}

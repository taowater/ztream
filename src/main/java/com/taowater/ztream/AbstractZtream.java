package com.taowater.ztream;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 抽象增强流
 *
 * @author Zhu56
 * @date 2022/11/13 21:37:12
 */
abstract class AbstractZtream<T, S extends AbstractZtream<T, S>> implements ZFilter<T, S>, ZSort<T, S>, ZCollect<T>,
        ZMath<T>,
        ZJoin<T, S> {
    protected final Stream<T> stream;

    protected AbstractZtream(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public Stream<T> unwrap() {
        return stream;
    }

    /**
     * 按某属性去重
     *
     * @param fun      属性
     * @param override 是否向前覆盖
     * @return {@link S }
     */
    public S distinct(Function<? super T, ?> fun, boolean override) {
        return wrap(handle(override, ZSort::reverse).map(t -> {
            Object v = null;
            if (Objects.nonNull(t)) {
                v = fun.apply(t);
            }
            return new PairBox<>(t, v);
        }).distinct().map(box -> box.a));
    }

    /**
     * 去重
     *
     * @param fun 属性
     * @return {@link S }
     */
    public S distinct(Function<? super T, ?> fun) {
        return distinct(fun, true);
    }

    /**
     * 去重
     *
     * @param override 是否向前覆盖
     * @return {@link S }
     */
    public S distinct(boolean override) {
        return wrap(handle(override, ZSort::reverse).distinct());
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
     * 随机取一个
     *
     * @return {@link Any}<{@link T}>
     */
    public Any<T> random() {
        return Any.of(shuffle().findFirst().orElse(null));
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


    static class Box<A> implements Consumer<A> {
        A a;

        Box() {
        }

        Box(A obj) {
            this.a = obj;
        }

        @Override
        public void accept(A a) {
            this.a = a;
        }
    }

    static class PairBox<A, B> extends Box<A> {
        B b;

        PairBox(A a, B b) {
            super(a);
            this.b = b;
        }

        static <T> PairBox<T, T> single(T a) {
            return new PairBox<>(a, a);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(b);
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == PairBox.class && Objects.equals(b, ((PairBox<?, ?>) obj).b);
        }
    }
}

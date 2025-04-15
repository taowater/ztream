package com.taowater.ztream.assist;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.function.Consumer;


/**
 * 包装类
 *
 * @author zhu56
 */
@Setter
@Getter
@SuppressWarnings("unused")
public class Box<A> implements Consumer<A> {
    private A a;

    Box() {
    }

    public Box(A obj) {
        this.a = obj;
    }

    @Override
    public void accept(A a) {
        this.a = a;
    }

    @Getter
    public static class PairBox<A, B> extends Box<A> {
        private B b;

        public PairBox(A a, B b) {
            super(a);
            this.b = b;
        }

        public static <T> PairBox<T, T> single(T a) {
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



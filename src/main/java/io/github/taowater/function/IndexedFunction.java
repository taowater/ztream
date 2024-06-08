package io.github.taowater.function;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 索引功能
 *
 * @author zhu56
 * @version 1.0
 * @date 2023/05/11 00:03
 */
public class IndexedFunction<T, R> implements Function<T, R> {
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
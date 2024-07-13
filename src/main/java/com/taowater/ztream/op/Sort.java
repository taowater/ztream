package com.taowater.ztream.op;

import com.taowater.ztream.IZtream;
import com.taowater.ztream.assist.Box;
import com.taowater.ztream.assist.Sorter;
import org.dromara.hutool.core.util.RandomUtil;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 排序操作
 *
 * @author zhu56
 * @date 2024/07/13 00:39
 */
public interface Sort<T, S extends IZtream<T, S>> extends IZtream<T, S> {

    /**
     * 升序
     *
     * @param fun 属性
     * @return 新流
     */
    default <U extends Comparable<? super U>> S asc(Function<? super T, ? extends U> fun) {
        return asc(fun, true);
    }

    /**
     * 升序
     *
     * @param fun       属性
     * @param nullFirst 是否null值前置
     * @return 新流
     */
    default <U extends Comparable<? super U>> S asc(Function<? super T, ? extends U> fun, boolean nullFirst) {
        return sort(r -> r.asc(fun, nullFirst));
    }

    /**
     * 升序
     *
     * @return 新流
     */
    default S asc() {
        return asc(true);
    }

    /**
     * 升序
     *
     * @param nullFirst 是否null值前置
     * @return 新流
     */
    @SuppressWarnings("unchecked")
    default S asc(boolean nullFirst) {
        return sort(r -> r.then((Comparator<T>) Comparator.naturalOrder(), nullFirst));
    }

    /**
     * 降序
     *
     * @return 新流
     */
    default <U extends Comparable<? super U>> S desc(Function<? super T, ? extends U> fun) {
        return desc(fun, true);
    }

    /**
     * 降序
     *
     * @param fun       属性
     * @param nullFirst 是否null值前置
     * @return 新流
     */
    default <U extends Comparable<? super U>> S desc(Function<? super T, ? extends U> fun, boolean nullFirst) {
        return sort(r -> r.desc(fun, nullFirst));
    }

    /**
     * 降序
     *
     * @return 新流
     */
    default S desc() {
        return desc(true);
    }

    /**
     * 降序
     *
     * @param nullFirst 是否null值前置
     * @return 新流
     */
    @SuppressWarnings("unchecked")
    default S desc(boolean nullFirst) {
        return sort(r -> r.then((Comparator<T>) Comparator.reverseOrder(), nullFirst));
    }

    /**
     * 排序
     *
     * @param consumer 排序上下分的消费函数
     * @return 新流
     */
    default S sort(Consumer<Sorter<T>> consumer) {
        Sorter<T> sorter = new Sorter<>();
        consumer.accept(sorter);
        return sorted(sorter.getComparator());
    }

    /**
     * 对元素进行洗牌
     *
     * @return 新流
     */
    default S shuffle() {
        return wrap(map(e -> new Box.PairBox<>(e, RandomUtil.randomInt())).sorted(Comparator.comparing(Box.PairBox::getB)).map(Box::getA));
    }

    /**
     * 反转顺序
     *
     * @return {@link S }
     */
    @SuppressWarnings("unchecked")
    default S reverse() {
        AtomicInteger index = new AtomicInteger(0);
        return wrap(map(e -> new Box.PairBox<>(e, index.getAndAdd(1))).sorted(Comparator.comparing((Box.PairBox<T, Integer> o) -> o.getB()).reversed()).map(Box::getA));
    }
}

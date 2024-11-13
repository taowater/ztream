package com.taowater.ztream.op.sort;

import com.taowater.ztream.IZtream;
import com.taowater.ztream.assist.Box;
import org.dromara.hutool.core.util.RandomUtil;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 排序操作
 *
 * @author zhu56
 * @since 0.0.3
 */
public interface Sort<T, S extends IZtream<T, S>> extends IZtream<T, S>, Asc<T, S>, Desc<T, S> {

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
     * 洗牌
     *
     * @return 新流
     */
    default S shuffle() {
        return shuffle(true);
    }

    /**
     * 洗牌
     *
     * @param condition 执行条件
     * @return {@link S }
     */
    default S shuffle(boolean condition) {
        if (!condition) {
            return ztream(this);
        }
        return ztream(map(e -> new Box.PairBox<>(e, RandomUtil.randomInt())).sorted(Comparator.comparing(Box.PairBox::getB)).map(Box::getA));
    }

    /**
     * 反转顺序
     *
     * @return {@link S }
     */
    default S reverse() {
        return reverse(true);
    }

    /**
     * 反转顺序
     *
     * @param condition 执行条件
     */
    default S reverse(boolean condition) {
        if (!condition) {
            return ztream(this);
        }
        AtomicInteger index = new AtomicInteger(0);
        return ztream(map(e -> new Box.PairBox<>(e, index.getAndAdd(1))).sorted(Comparator.comparing((Function<Box.PairBox<T, Integer>, Integer>) Box.PairBox::getB).reversed()).map(Box::getA));
    }

    @Override
    default <U extends Comparable<? super U>> S desc(boolean condition, Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return sort(r -> r.desc(condition, keyExtractor, nullFirst));
    }

    @Override
    default S desc(boolean condition, Comparator<? super T> comparator, boolean nullFirst) {
        return sort(r -> r.desc(condition, comparator, nullFirst));
    }

    @Override
    default <U extends Comparable<? super U>> S asc(boolean condition, Function<? super T, ? extends U> keyExtractor, boolean nullFirst) {
        return sort(r -> r.asc(condition, keyExtractor, nullFirst));
    }

    @Override
    default S asc(boolean condition, Comparator<? super T> comparator, boolean nullFirst) {
        return sort(r -> r.asc(condition, comparator, nullFirst));
    }
}

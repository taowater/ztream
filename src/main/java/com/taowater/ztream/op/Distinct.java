package com.taowater.ztream.op;

import com.taowater.ztream.Any;
import com.taowater.ztream.IZtream;
import com.taowater.ztream.assist.Box;

import java.util.function.Function;

/**
 * 去重相关操作
 *
 * @author zhu56
 */
public interface Distinct<T, S extends IZtream<T, S>> extends IZtream<T, S> {

    /**
     * 按某属性去重
     *
     * @param fun 属性
     */
    default S distinct(Function<? super T, ?> fun) {
        return distinct(true, fun);
    }

    /**
     * 按某属性去重
     *
     * @param condition 执行条件
     * @param fun       属性
     */
    default S distinct(boolean condition, Function<? super T, ?> fun) {
        if (!condition) {
            return ztream(this);
        }
        return ztream(map(t -> new Box.PairBox<>(t, Any.of(t).get(fun)))
                .distinct()
                .map(Box::getA));
    }

    /**
     * 去重
     *
     * @param condition 执行条件
     */
    default S distinct(boolean condition) {
        if (!condition) {
            return ztream(this);
        }
        return distinct();
    }
}

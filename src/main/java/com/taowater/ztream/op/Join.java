package com.taowater.ztream.op;


import com.taowater.ztream.Any;
import com.taowater.ztream.IZtream;
import com.taowater.ztream.assist.ExCollectors;

import java.util.Objects;
import java.util.function.Function;

/**
 * join操作
 *
 * @author Zhu56
 * @since 0.0.1
 */
public interface Join<T, S extends IZtream<T, S>> extends IZtream<T, S> {

    /**
     * 返回拼接后的字符串
     *
     * @return 拼接后的字符串
     */
    default String join() {
        return join(",");
    }

    /**
     * 返回拼接后的字符串
     *
     * @param delimiter 分隔符
     * @return 拼接后的字符串
     */
    default String join(CharSequence delimiter) {
        return join(delimiter, "", "");
    }

    /**
     * 拼接属性
     *
     * @param fun 函数
     * @return 拼接后的字符串
     */
    default String join(Function<? super T, ?> fun) {
        return join(fun, ",");
    }

    /**
     * 拼接属性，指定分隔符
     *
     * @param fun       函数
     * @param delimiter 分隔符
     * @return 拼接后的字符串
     */
    default String join(Function<? super T, ?> fun, CharSequence delimiter) {
        return map(e -> Any.of(e).get(fun)).collect(ExCollectors.join(delimiter, "", ""));
    }

    /**
     * 返回拼接后的字符串
     *
     * @param delimiter 分隔符
     * @param prefix    前缀
     * @param suffix    后缀
     * @return 拼接后的字符串
     */
    default String join(CharSequence delimiter,
                        CharSequence prefix,
                        CharSequence suffix) {
        if (Objects.isNull(delimiter)) {
            delimiter = ",";
        }
        return collect(ExCollectors.join(delimiter, prefix, suffix));
    }
}

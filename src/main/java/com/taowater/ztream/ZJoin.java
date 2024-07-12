package com.taowater.ztream;


import java.util.Objects;
import java.util.function.Function;

/**
 * join操作
 *
 * @author Zhu56
 * @date 2022/11/13 19:12:35
 */
interface ZJoin<T, S extends IZtream<T, S>> extends IZtream<T, S> {

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

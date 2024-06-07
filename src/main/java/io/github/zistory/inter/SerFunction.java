package io.github.zistory.inter;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 序列化的方法引用
 *
 * @author zhu56
 * @version 1.0
 * @date 2022/4/29 16:27
 */
@FunctionalInterface
public interface SerFunction<T, R> extends Function<T, R>, Serializable {
}

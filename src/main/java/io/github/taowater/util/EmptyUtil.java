package io.github.taowater.util;

import lombok.experimental.UtilityClass;
import org.dromara.hutool.core.map.MapUtil;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 判空工具类
 *
 * @author Zhu56
 * @date 2021/10/16 2:05
 */
@UtilityClass
public class EmptyUtil {

    /**
     * 情形与判断方法的映射map
     */
    private final Map<Predicate<Object>, Predicate<Object>> STRATEGY_MAP = MapUtil
            .builder(new LinkedHashMap<Predicate<Object>, Predicate<Object>>())
            .put(Objects::isNull, o -> true)
            .put(CharSequence.class::isInstance, o -> ((CharSequence) o).length() == 0)
            .put(Iterator.class::isInstance, o -> !((Iterator<?>) o).hasNext())
            .put(Iterable.class::isInstance, o -> EmptyUtil.isEmpty(((Iterable<?>) o).iterator()))
            .put(Map.class::isInstance, o -> ((Map<?, ?>) o).isEmpty())
            .put(o -> o.getClass().isArray(), o -> Array.getLength(o) == 0)
            .put(o -> true, o -> false)
            .build();

    /**
     * 判断任意对象是否为空
     *
     * @param obj 任意对象
     * @return 判断结果
     */
    public boolean isEmpty(Object obj) {
        return STRATEGY_MAP.entrySet()
                .stream()
                .filter(e -> e.getKey().test(obj))
                .findFirst()
                .map(Map.Entry::getValue)
                .map(e -> e.test(obj))
                .orElse(false);
    }

    /**
     * 判断任意对象是否不为空
     *
     * @param obj 任意对象
     * @return 判断结果
     */
    public boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断对象是否至少一个为空 主要包括字符串，集合，数组，及其他类型
     *
     * @param objs 若干个对象
     * @return 判断结果
     */
    public boolean isHadEmpty(Object... objs) {
        return Stream.of(objs).anyMatch(EmptyUtil::isEmpty);
    }

    /**
     * 判断若干个对象中是否存在非空
     *
     * @param objs 若干个对象
     * @return 判断结果
     */
    public boolean isHadNotEmpty(Object... objs) {
        return Stream.of(objs).anyMatch(EmptyUtil::isNotEmpty);
    }

    /**
     * 判断若干个对象是否全空
     *
     * @param objs 若干个对象
     * @return 判断结果
     */
    public boolean isAllEmpty(Object... objs) {
        return !isHadNotEmpty(objs);
    }

    /**
     * 判断若干个对象中是否全为非空
     *
     * @param objs 若干个对象
     * @return 判断结果
     */
    public boolean isAllNotEmpty(Object... objs) {
        return !isHadEmpty(objs);
    }

    /**
     * 判断若干个对象中是否既有空的，也有非空的
     *
     * @param objs 若干个对象
     * @return 判断结果
     */
    public boolean isHadBoth(Object... objs) {
        return isHadEmpty(objs) && isHadNotEmpty(objs);
    }
}

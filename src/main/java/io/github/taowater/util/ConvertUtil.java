package io.github.taowater.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.sf.cglib.beans.BeanCopier;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 转换工具
 *
 * @author zhu56
 * @date 2023/07/01 01:07
 */
@UtilityClass
public class ConvertUtil {

    private static final Map<String, BeanCopier> MAP = new ConcurrentHashMap<>();

    private String buildKey(Class<?> sClazz, Class<?> tClazz) {
        return MessageFormat.format("{0}convert{1}", sClazz.getName(), tClazz.getName());
    }

    /**
     * 转换
     *
     * @param source 源
     * @param tClazz 目标类型
     * @return {@link T}
     */
    @SneakyThrows({
            NoSuchMethodException.class,
            SecurityException.class,
            InstantiationException.class,
            IllegalAccessException.class,
            IllegalArgumentException.class,
            InvocationTargetException.class
    })
    public <S, T> T convert(S source, Class<T> tClazz) {
        T target = tClazz.getConstructor().newInstance();
        copy(source, target);
        return target;
    }

    /**
     * 创建拷贝器
     *
     * @param sClass 源类型
     * @param tClazz 目标类型
     * @return {@link BeanCopier}
     */
    private static BeanCopier createCopier(Class<?> sClass, Class<?> tClazz) {
        return BeanCopier.create(sClass, tClazz, false);
    }

    /**
     * 获得拷贝器
     *
     * @param sClazz 源类型
     * @param tClazz 目标类型
     * @return {@link BeanCopier}
     */
    private static BeanCopier getCopier(Class<?> sClazz, Class<?> tClazz) {
        return MAP.computeIfAbsent(buildKey(sClazz, tClazz), key -> createCopier(sClazz, tClazz));
    }

    /**
     * 复制
     *
     * @param source 源
     * @param target 目标
     */
    public <S, T> void copy(S source, T target) {
        BeanCopier copier = getCopier(source.getClass(), target.getClass());
        copier.copy(source, target, null);
    }

}

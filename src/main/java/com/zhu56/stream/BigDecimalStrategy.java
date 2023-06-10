package com.zhu56.stream;

import cn.hutool.core.map.MapUtil;
import com.zhu56.inter.SerFunction;
import com.zhu56.util.EmptyUtil;
import com.zhu56.util.LambdaUtil;
import lombok.experimental.UtilityClass;
import lombok.var;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 数字策略
 *
 * @author 朱滔
 * @version 1.0
 * @date 2022/4/29 17:14
 */
@UtilityClass
public class BigDecimalStrategy {

    private final Map<Class<?>, Function<BigDecimal, ?>> TYPE_FUN = MapUtil.builder(new HashMap<Class<?>, Function<BigDecimal, ?>>())
            .put(Byte.class, BigDecimal::byteValue)
            .put(Short.class, BigDecimal::shortValue)
            .put(Integer.class, BigDecimal::intValue)
            .put(BigInteger.class, BigDecimal::toBigInteger)
            .put(Long.class, BigDecimal::longValue)
            .put(Float.class, BigDecimal::floatValue)
            .put(Double.class, BigDecimal::doubleValue)
            .put(null, Function.identity())
            .build();

    /**
     * 获取转换函数
     *
     * @param clazz clazz
     * @return {@link Function}<{@link BigDecimal}, {@link T}>
     */
    @SuppressWarnings("unchecked")
    public <T extends Number> Function<BigDecimal, T> getFunction(Class<T> clazz) {
        return (Function<BigDecimal, T>) TYPE_FUN.getOrDefault(clazz, Function.identity());
    }

    /**
     * 获得指定类型数值
     *
     * @param bigDecimal 大小数
     * @param function   函数
     * @return {@link N}
     */
    public <N extends Number> N getValue(BigDecimal bigDecimal, SerFunction<?, N> function) {
        if (EmptyUtil.isHadEmpty(bigDecimal, function)) {
            return null;
        }
        var returnClass = LambdaUtil.getReturnClass(function);
        var fun = BigDecimalStrategy.getFunction(returnClass);
        return fun.apply(bigDecimal);
    }
}

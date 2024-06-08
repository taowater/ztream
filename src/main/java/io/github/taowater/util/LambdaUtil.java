package io.github.taowater.util;

import io.github.taowater.inter.SerFunction;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.dromara.hutool.core.reflect.method.MethodUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * λ表达式工具类
 *
 * @author 朱滔
 * @date 2021/10/10 23:42
 */
@UtilityClass
public class LambdaUtil {

    /**
     * 类型λ缓存
     */
    private final Map<Class<?>, SerializedLambda> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();

    /**
     * 返回类型模式
     */
    private final Pattern RETURN_TYPE_PATTERN = Pattern.compile("\\(.*\\)L(.*);");

    /**
     * 获得一个方法引用的lambda实例
     *
     * @param fun 函数
     * @return {@link SerializedLambda}
     */
    public <T, R> SerializedLambda getLambda(SerFunction<T, R> fun) {
        return getSerializedLambda(fun);
    }


    /**
     * 获取方法的lambda实例
     *
     * @param fun 有趣
     * @return {@link SerializedLambda}
     */
    @SneakyThrows
    public SerializedLambda getSerializedLambda(Serializable fun) {
        Class<?> funClazz = fun.getClass();
        return CLASS_LAMBDA_CACHE.computeIfAbsent(funClazz, c -> {
            Method method = MethodUtil.getMethodByName(funClazz, "writeReplace");
            return MethodUtil.invoke(fun, method);
        });
    }

    /**
     * 得到返回值的类型
     *
     * @param fun 有趣
     * @return {@link Class}<{@link R}>
     */
    @SuppressWarnings("unchecked")
    public <T, R> Class<R> getReturnClass(SerFunction<T, R> fun) {
        SerializedLambda serializedLambda = getSerializedLambda(fun);
        String expr = serializedLambda.getImplMethodSignature();
        Matcher matcher = RETURN_TYPE_PATTERN.matcher(expr);
        if (!matcher.find() || matcher.groupCount() != 1) {
            return null;
        }
        String className = matcher.group(1).replace("/", ".");
        Class<R> clazz;
        try {
            clazz = (Class<R>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
        return clazz;
    }


}

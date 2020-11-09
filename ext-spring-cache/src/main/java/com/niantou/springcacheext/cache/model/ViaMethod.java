package com.niantou.springcacheext.cache.model;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.support.ExtCacheHelper;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * 此类隔绝方法被代理前后的不等性， 使代理前后方法对应的ViaMethod相等
 * <p>
 * 示例说明： 假设class被代理前的methodA对应的是viaMethodA， class被代理后(class$$xxx)的methodB对应的是viaMethodB。
 *           此时比较methodA与methodB是不等的，但是比较viaMethodA viaMethodB是相等的
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 3:53:07
 */
@Slf4j
@Getter
@ToString
@EqualsAndHashCode
@Setter(AccessLevel.PRIVATE)
public class ViaMethod {
    
    /**
     * 代理类分隔符
     * <p>
     * 注意: 这里只用一个$是不行的， 因为一个$是内部类的分隔符；代理类的分割符是$$
     */
    private static final String CLASS_PROXY_DELIMITER = "$$";
    
    private ViaMethod() {
    }
    
    /** 全类名 */
    private String className;
    
    /** 方法名 */
    private String methodName;
    
    /** 方法参数个数 */
    private int parameterCount;
    
    /** 方法参数类型 */
    private Class<?>[] parameterTypes;
    
    /** 方法返回值类型 */
    private Class<?> returnType;
    
    /**
     * 获取ViaMethod实例
     *
     * @param method
     *         方法
     *
     * @return ViaMethod实例
     */
    public static ViaMethod getInstance(@Nullable Method method) {
        ViaMethod viaMethod = new ViaMethod();
        //noinspection ConstantConditions
        String originClassName = method.getDeclaringClass().getName();
        String className = originClassName;
        if (originClassName.contains(CLASS_PROXY_DELIMITER)) {
            className = originClassName.substring(0, originClassName.indexOf(CLASS_PROXY_DELIMITER));
            if (log.isDebugEnabled()) {
                log.debug(ExtCacheHelper.LOG_PREFIX + " convert originClassName [{}] to [{}]", originClassName, className);
            }
        }
        viaMethod.setClassName(className);
        viaMethod.setMethodName(method.getName());
        viaMethod.setParameterCount(method.getParameterCount());
        viaMethod.setParameterTypes(method.getParameterTypes());
        viaMethod.setReturnType(method.getReturnType());
        return viaMethod;
    }
}

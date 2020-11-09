package com.niantou.springcacheext.cache.support;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.model.ExtCacheCounter;
import com.niantou.springcacheext.cache.model.ViaMethod;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * 环绕处理器
 *
 * <p>
 * 需保证，{@link ExtCacheAroundHandlerAdvice}的优先级比ext-spring-cache代理的优先级高
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 2:29:05
 */
@Aspect
public class ExtCacheAroundHandlerAdvice implements Ordered {
    
    /** 此ORDER的值需要比org.springframework.aop.interceptor.ExposeInvocationInterceptor#getOrder()的值大 */
    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 2;
    
    public static final String BEAN_NAME = "extCacheAroundHandler";

    @Pointcut("@annotation(com.niantou.springcacheext.cache.annotation.ExtCacheable)")
    public void point() {
    }

    @Before(value = "point()")
    public void beforeAdvice(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Stack<ExtCacheCounter> stack = SafeContainer.THREAD_LOCAL_CACHE_COUNTER.get();
        if (stack == null) {
            stack = new Stack<>();
            SafeContainer.THREAD_LOCAL_CACHE_COUNTER.set(stack);
        }
        int preIndex;
        if (stack.empty()) {
            preIndex = -1;
        } else {
            preIndex = stack.peek().getIndex();
        }
        stack.push(
                ExtCacheCounter.builder()
                        .index(preIndex + 1)
                        .viaMethod(ViaMethod.getInstance(method))
                        .build()
        );
    }
    
    @AfterReturning(value = "point()")
    public void afterReturningAdvice() {
        Stack<ExtCacheCounter> stack = SafeContainer.THREAD_LOCAL_CACHE_COUNTER.get();
        if (stack == null) {
            return;
        }
        if (stack.isEmpty()) {
            SafeContainer.THREAD_LOCAL_CACHE_COUNTER.remove();
            return;
        }
        stack.pop();
        if (stack.isEmpty()) {
            SafeContainer.THREAD_LOCAL_CACHE_COUNTER.remove();
        }
    }
    
    @AfterThrowing(value = "point()")
    public void afterThrowingAdvice() {
        afterReturningAdvice();
    }
   
    
    @Override
    public int getOrder() {
        return ORDER;
    }
}
package com.niantou.springcacheext.cache.support;

import com.niantou.springcacheext.author.JustryDeng;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;

/**
 * 空实现
 *
 * @author {@link JustryDeng}
 * @since 2020/11/9 21:51:19
 */
@SuppressWarnings("NullableProblems")
public class EmptyObjectProvider<T> implements ObjectProvider<T> {
    
    @Override
    public T getObject(Object... args) throws BeansException {
        return null;
    }
    
    @Override
    public T getIfAvailable() throws BeansException {
        return null;
    }
    
    @Override
    public T getIfUnique() throws BeansException {
        return null;
    }
    
    @Override
    public T getObject() throws BeansException {
        return null;
    }
}

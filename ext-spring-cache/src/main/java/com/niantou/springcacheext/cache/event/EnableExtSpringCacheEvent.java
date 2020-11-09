package com.niantou.springcacheext.cache.event;

import com.niantou.springcacheext.author.JustryDeng;
import org.springframework.context.ApplicationEvent;

/**
 * 启用ext-spring-cache事件
 *
 * @author {@link JustryDeng}
 * @since 2020/11/7 15:03:05
 */
public class EnableExtSpringCacheEvent extends ApplicationEvent {
    
    /**
     * 构造器
     *
     * @param source
     *         该事件的相关数据
     *
     * @date 2019/11/19 6:40
     */
    public EnableExtSpringCacheEvent(Object source) {
        super(source);
    }
}


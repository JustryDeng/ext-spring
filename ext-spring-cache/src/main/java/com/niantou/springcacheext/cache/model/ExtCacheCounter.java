package com.niantou.springcacheext.cache.model;

import com.niantou.springcacheext.author.JustryDeng;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (代理嵌套)计数器
 * <p>
 * P.S. 防止在嵌套代理时，错乱清除ThreadLocal。
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 2:42:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtCacheCounter {
    
    /**
     * 层级
     * <p>
     *  此字段暂时没用上， 但是给老夫一种预感， 后面可能会用上， 作为保留字段吧
     */
    private int index;
    
    /**
     * index对应的标识方法的ViaMethod对象
     */
    private ViaMethod viaMethod;
}

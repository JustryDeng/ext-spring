package com.niantou.springcacheext;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.EnableExtCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;

/**
 * start-class
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 12:53:45
 */
@EnableExtCache
@SpringBootApplication
public class SpringCacheExtApplication  {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringCacheExtApplication.class, args);
    }
    
}

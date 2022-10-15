package com.xkcoding.cache.redis.service.impl;

import com.google.common.collect.Maps;
import com.xkcoding.cache.redis.entity.User;
import com.xkcoding.cache.redis.service.CacheService;
import com.xkcoding.cache.redis.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * <p>
 * UserService
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-15 16:45
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static int  nThreads = 100000;
//    private  static ExecutorService  executorService = Executors.newFixedThreadPool(nThreads);
    private  static ExecutorService  executorService = new ThreadPoolExecutor(
        16,
    64,
    60,
    TimeUnit.MINUTES,
    new LinkedBlockingQueue<>(nThreads));

    @Override
    public void incrCount() {
        // 测试线程安全，程序结束查看redis中count的值是否为1000
        long start = Instant.now().toEpochMilli();
        log.info("【start】= {}", start);
        CountDownLatch threadLatchs = new CountDownLatch(nThreads);
        IntStream.range(0, nThreads).forEach(
            i -> executorService.execute(() -> {
                stringRedisTemplate.opsForValue().increment("count", 1);
                threadLatchs.countDown();
            })
        );
        try {
            threadLatchs.await();  //等待主线程执行
        } catch (Throwable e) {
            e.printStackTrace();
        }
        long end = Instant.now().toEpochMilli();
        log.info("【start】= {}, 【end】= {}, 【time】= {}", start, end, end - start);
        //  100000 此更新如下
//【start】= 1665815156457, 【end】= 1665815160849, 【time】= 4552
//        【start】= 1665815578661, 【end】= 1665815581264, 【time】= 2603
//        【start】= 1665816135849, 【end】= 1665816138739, 【time】= 2890
    }
}

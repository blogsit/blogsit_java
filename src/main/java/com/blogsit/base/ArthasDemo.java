package com.blogsit.base;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArthasDemo {
    public static HashSet hashSet = new HashSet();
    /**
     *
     * 线程池 大小1
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static final Logger logger = LoggerFactory.getLogger(ArthasDemo.class);

    public static void main(String[] args) {
        cpu();
        addHashSet();
        thread();
        deadThread();
        //死锁
        deadThread();
    }

    /**
     * 不断向 hashSet 里面 集合添加数据
     */
    public static void addHashSet() {
        new Thread(() -> {
            int count = 0;
            while (true) {
                try {
                    hashSet.add("count" + count);
                    Thread.sleep(100);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void cpu() {
        cpuHigh();
        cpuNormal();
    }

    /**
     * 极度消耗CPU的线程
     */
    public static void cpuHigh() {
        Thread thread = new Thread(() -> {
            while (true) {
                logger.info("cpu start 100");
            }
        });
        executorService.submit(thread);
    }

    /**
     * 普通消耗CPU的线程
     */
    private static  void cpuNormal(){
        for (int i = 0; i <10 ; i++) {
            new Thread(()->{while (true){
                logger.info("CPU start");
                try {
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }}).start();
        }
    }
    /**
     *
     */
    public static void thread() {
        Thread thread = new Thread(() -> {
            while (true) {
                logger.debug("thread start");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        executorService.submit(thread);
    }
    /**
     * 死锁
     */
    private  static  void deadThread(){
        Object resourceA = new Object();
        Object resourceB = new Object();
        Thread threadA = new Thread(()->{
            synchronized (resourceA){
                logger.info(Thread.currentThread() + "get Resource A");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info(Thread.currentThread() + "waiting get resourceB");
                synchronized (resourceB){
                    logger.info(Thread.currentThread() + "get Resource B");
                }
            }
        });
        Thread threadB = new Thread(()->{
            synchronized (resourceB){
                logger.info(Thread.currentThread() + "get Resource B");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info(Thread.currentThread() + "waiting get resourceA");
                synchronized (resourceA){
                    logger.info(Thread.currentThread() + "get Resource A");
                }
            }
        });

        threadA.start();
        threadB.start();
    }

}

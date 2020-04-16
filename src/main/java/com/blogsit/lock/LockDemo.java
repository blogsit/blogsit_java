package com.blogsit.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {
    private static final Lock lock = new ReentrantLock(false);

    public static void main(String[] args) {
     /*   for (int i = 0; i <100 ; i++) {
            new Thread(() -> test(), "线程"+i ).start();
        }*/
        new Thread(() -> test1(), "线程A").start();
        new Thread(() -> test1(), "线程B").start();
        new Thread(() -> test1(), "线程C").start();
        new Thread(() -> test1(), "线程D").start();
        new Thread(() -> test1(), "线程E").start();
    }

    public static void test() {
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "获取锁");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + "释放锁");
            lock.unlock();
        }
    }
    public static void test1() {
        for (int i = 0; i < 2; i++) {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + "获取锁");
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}

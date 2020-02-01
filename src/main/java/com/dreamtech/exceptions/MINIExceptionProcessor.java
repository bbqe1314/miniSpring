package com.dreamtech.exceptions;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dreamtech.utils.ApplicationUtil;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MINIExceptionProcessor {

    private Logger logger = LogManager.getLogger(MINIExceptionProcessor.class);

    private HashMap<String, Exception> exceptions;

    private Lock exLock = new ReentrantLock();
    private Condition exCondition = exLock.newCondition();

    private static class SingletonExceptionProcessor {
        static final MINIExceptionProcessor instance = new MINIExceptionProcessor();
    }


    /**
     * 单例模式 获取实例
     */
    public static MINIExceptionProcessor getInstance() {
        return MINIExceptionProcessor.SingletonExceptionProcessor.instance;
    }

    private MINIExceptionProcessor() {
        exceptions = new HashMap<>();
        Thread exceptionThread = new Thread(() -> {
            exLock.lock();
            while (true) {
                try {
                    if (this.exceptions.size() > 0) {
                        System.out.println(ApplicationUtil.currentTime() + " : " + "Start printing exception information");
                        printError();
                        clearError();
                    }
                    System.out.println(ApplicationUtil.currentTime() + " : " + "There is no exception temporarily, the print thread is suspended");
                    exCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        exceptionThread.setName("exception-thread");
        exceptionThread.start();
    }


    /**
     * 添加异常
     */
    public void putException(String errorName, Exception ex) {
        this.exceptions.put(errorName, ex);
        exLock.lock();
        exCondition.signal();
        exLock.unlock();
    }

    /**
     * 打印异常
     */
    private synchronized void printError() {
        this.exceptions.forEach((tip, e) -> {
            logger.error(String.format(tip + "[%s]", e.toString()));
        });
    }

    public HashMap<String, Exception> getExceptions() {
        return exceptions;
    }

    /**
     * 清除异常
     */
    private synchronized void clearError() {
        this.exceptions.clear();
    }
}

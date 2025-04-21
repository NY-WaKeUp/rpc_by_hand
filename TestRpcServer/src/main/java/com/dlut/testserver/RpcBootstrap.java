package com.dlut.testserver;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 启动类，负责初始化Spring上下文并启动RPC服务。
 */
public class RpcBootstrap {

    public static void main(String[] args) {
        // 加载Spring配置文件并初始化Spring上下文
        new ClassPathXmlApplicationContext("spring.xml");
    }

}

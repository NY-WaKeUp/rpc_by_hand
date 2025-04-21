package com.dlut.testclient;

import com.dlut.rpc_client.rpcProxy;
import com.dlut.testserviceapi.testService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 测试客户端程序，用于演示如何使用RPC（远程过程调用）代理来调用远程服务。
 */
public class testClient {

    public static void main(String[] args) throws Exception {

        // 初始化Spring应用上下文，加载配置文件"spring.xml"
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        // 从Spring上下文中获取RpcProxy的实例，即动态代理对象
        rpcProxy rpcProxy = context.getBean(rpcProxy.class);

       /**
        * 测试HelloService接口的实现类1
        * 调用RpcProxy对象的create方法来创建RPC代理接口
        */
        testService helloServiceImpl1 = rpcProxy.create(testService.class);
        // 调用hello方法并打印结果，就像本地调用一样
        String result = helloServiceImpl1.hello("Jackxxxxxx");
        System.out.println(result);

        // 使用RpcProxy创建一个HelloService的代理实例，指定使用名为"TestServiceImpl2"的实现
        testService helloServiceImpl2 = rpcProxy.create(testService.class, "2.0");
        // 调用hello方法并打印结果
        String result2 = helloServiceImpl2.hello("Tomxxxxxx");
        System.out.println(result2);

        // 正常退出程序
        System.exit(0);
    }

}


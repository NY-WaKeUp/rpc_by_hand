package com.dlut.rpc_client;


import com.dlut.rpc_common.model.rpcRequest;
import com.dlut.rpc_common.model.rpcResponse;
import com.dlut.rpc_registry.ServiceDiscovery;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.UUID;

public class rpcProxy {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(rpcProxy.class);

    /**
     * 服务地址
     */
    private String serviceAddress;

    /**
     * 服务发现组件
     */

    private ServiceDiscovery serviceDiscovery;

    /**
     * 构造函数
     * @param serviceDiscovery 服务发现组件
     */

    public rpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 对 send 方法进行增强
     * @param interfaceClass
     * @param <T> 返回接口实例
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }
    /**
     * 使用 CGLIB 动态代理机制创建一个指定接口的代理对象，并通过 RPC 调用远程服务。
     *
     * @param <T> 返回的代理对象类型，通常为接口类型
     * @param interfaceClass 需要代理的接口类
     * @param serviceVersion 服务版本号，用于区分不同版本的服务
     * @return 返回一个实现了指定接口的代理对象
     * @throws RuntimeException 如果服务地址为空或 RPC 响应为空，则抛出异常
     */
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        // 使用 CGLIB 动态代理机制
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(interfaceClass.getClassLoader());
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new MethodInterceptor() {
            /**
             * 拦截代理对象的方法调用，将其转换为 RPC 请求并发送到远程服务。
             *
             * @param o 被代理的对象（需要增强的对象）
             * @param method 被拦截的方法（需要增强的方法）
             * @param args 方法入参
             * @param methodProxy 用于调用原始方法
             * @return 返回远程服务的调用结果
             * @throws Throwable 如果远程调用过程中发生异常，则抛出
             */
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                // 创建 RPC 请求并设置属性
                rpcRequest rpcRequest = new rpcRequest();
                rpcRequest.setRequestId(UUID.randomUUID().toString());
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParameterTypes(method.getParameterTypes());
                rpcRequest.setParameters(args);
                rpcRequest.setInterfaceName(interfaceClass.getName());
                rpcRequest.setServiceVersion(serviceVersion);

                // 根据服务名称和版本号查询服务地址
                if (serviceDiscovery != null) {
                    String serviceName = interfaceClass.getName();
                    if (serviceVersion != null) {
                        String service_Version = serviceVersion.trim();
                        if (!StringUtils.isEmpty(service_Version)) {
                            serviceName += "-" + service_Version;
                        }
                    }
                    // 获取服务地址（用于建立连接）
                    serviceAddress = serviceDiscovery.discovery(serviceName);
                    LOGGER.info("discover service: {} => {}", serviceName, serviceAddress);
                }

                if (serviceAddress != null) {
                    serviceAddress = serviceAddress.trim();
                    if (StringUtils.isEmpty(serviceAddress)) {
                        throw new RuntimeException("server address is empty");
                    }
                }

                // 从服务地址中解析主机名与端口号
                String[] array = StringUtils.split(serviceAddress, ":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);

                // 创建 RPC 客户端对象，建立连接/发送请求/接收请求
                rpcClient client = new rpcClient(host, port);
                long time = System.currentTimeMillis(); // 当前时间
                rpcResponse rpcResponse = client.send(rpcRequest);
                System.out.println(rpcResponse);
                LOGGER.info("time: {}ms", System.currentTimeMillis() - time);
                if (rpcResponse == null) {
                    throw new RuntimeException("response is null");
                }
                if (rpcResponse.hasException()) {
                    throw rpcResponse.getException();
                }
                else {
                    return rpcResponse.getResult();
                }
            }
        });

        return (T) enhancer.create();
    }



}

package com.dlut.rpc_registry;
/**
 * ServiceRegistry 接口定义了一个服务注册的方法，用于将服务名称和服务地址进行注册。
 *
 */
public interface ServiceRegistry {
    /**
     * 注册服务名称与服务地址
     * @param serviceName 服务名称（被暴露的实现类的接口名称）
     * @param serviceAddress 服务地址（比如该服务被暴露在 Netty 的 8000 端口，则服务地址为 127.0.0.1:8000）
     */
    void register(String serviceName, String serviceAddress);
}


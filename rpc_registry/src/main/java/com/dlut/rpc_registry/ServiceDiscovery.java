package com.dlut.rpc_registry;


/**
 * ServiceDiscovery 接口定义了服务发现的功能。
 * 该接口主要用于根据服务名称查找并返回服务的地址或标识符。
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称进行服务发现，并返回服务的地址或标识符。
     *
     * @param serviceName 需要发现的服务名称，不能为 null 或空字符串。
     * @return 返回与指定服务名称对应的服务地址或标识符。如果未找到服务，则返回 null。
     */
    String discovery(String serviceName);
}


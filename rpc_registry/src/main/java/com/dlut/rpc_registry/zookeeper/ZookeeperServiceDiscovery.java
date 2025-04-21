package com.dlut.rpc_registry.zookeeper;


import com.dlut.rpc_registry.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/**
 * 使用 ZooKeeper 实现服务发现功能
 */

public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    /**
     * 注册/发现中心地址
     */
    private String zkAddress;
    /**
     * 构造函数
     * @param zkAddress 注册中心地址
     */
    public ZookeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    /**
     * 根据服务名称从Zookeeper中查找对应的服务地址。
     *
     * @param serviceName 服务名称，用于在Zookeeper中查找对应的服务节点。
     * @return 返回服务地址的字符串表示。如果找到多个地址，则随机返回一个。
     * @throws RuntimeException 如果无法找到服务节点或地址节点，则抛出运行时异常。
     */
    @Override
    public String discovery(String serviceName) {
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.info("connect zookeeper");

        try {
            // 根据 serviceName 查找 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }

            // 查找 address 节点
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtils.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }

            String address;
            int size = addressList.size();
            if (size == 1) {
                // 如果只有一个 address 节点，则直接获取该地址
                address = addressList.get(0);
                LOGGER.info("get only address node: {}", address);
            } else {
                // 负载均衡策略获取一个地址（这里选择了随机获取）
                address = addressList.get( ThreadLocalRandom.current().nextInt(size));
                LOGGER.info("get random address node: {}", address);
            }

            // 读取并返回地址节点的数据(服务地址)
            return zkClient.readData(servicePath + "/" + address);
        } finally {
            zkClient.close();
        }
    }

}

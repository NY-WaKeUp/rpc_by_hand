package com.dlut.rpc_registry.zookeeper;


import com.dlut.rpc_registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZookeeperServiceRegistry implements ServiceRegistry {

    /**
     * slf4j 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    /**
     * Zookeeper 客户端 ZkClient
     */
    private ZkClient zkClient;

    /**
     * 该构造方法提供给用户（用户通过配置文件指定 zkAddress 完成服务注册组件的注入）
     * @param zkAddress 注册中心地址
     */
    public ZookeeperServiceRegistry(String zkAddress) {
        zkClient = new ZkClient(zkAddress, 1000, 1000);
        LOGGER.info("connect zookeeper");
    }

    /**
     * 注册服务到Zookeeper。
     *
     * 该函数用于将指定的服务名称和服务地址注册到Zookeeper中。首先会检查并创建必要的持久节点，
     * 然后创建一个临时的顺序节点来存储服务地址。
     *
     * @param serviceName 服务名称，用于在Zookeeper中标识服务。
     * @param serviceAddress 服务地址，即服务的实际访问地址。
     */
    @Override
    public void register(String serviceName, String serviceAddress) {
        // 获取Zookeeper中的注册路径
        String registryPath = Constant.ZK_REGISTRY_PATH;

        // 如果注册路径不存在，则创建持久节点，该节点下存放所有的服务节点
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            LOGGER.info("create registry node: {}", registryPath);
        }

        // 构建服务路径，并检查是否存在，不存在则创建持久节点
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.info("create service node: {}", servicePath);
        }

        // 创建临时的顺序节点来存储服务地址
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        LOGGER.info("create address node: {}", addressNode);
    }
}

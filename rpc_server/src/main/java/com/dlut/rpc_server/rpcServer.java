package com.dlut.rpc_server;

import com.dlut.rpc_common.enc_dec.rpcDecoder;
import com.dlut.rpc_common.enc_dec.rpcEncoder;
import com.dlut.rpc_common.model.rpcRequest;
import com.dlut.rpc_common.model.rpcResponse;
import com.dlut.rpc_registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class rpcServer implements ApplicationContextAware, InitializingBean {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(rpcServer.class);
    /**
     * netty服务端地址
     */
    private String serviceAddress;

    /**
     * 服务注册组件
     */
    private ServiceRegistry serviceRegistry;

    /**
     * 服务端接口实现类，存储服务名称与服务对象之间的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    /**
     * 构造函数，提供给用户在配置文件中注入服务地址
     * @param serviceAddress
     */
    public rpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    /**
     * 构造函数，提供给用户在配置文件中注入服务地址和注册中心
     * @param serviceAddress
     * @param serviceRegistry
     */
    public rpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }


    /**
     * 在Spring Bean属性设置完成后执行的方法，用于初始化并启动RPC服务器。
     * 该方法会创建并配置Netty的NioEventLoopGroup、ServerBootstrap等组件，
     * 绑定指定的IP和端口，并注册服务到服务注册中心。
     *
     * @throws Exception 如果在启动或绑定过程中发生错误，抛出异常。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建主从线程组，用于处理连接和I/O操作
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 配置服务器启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 配置管道处理器，包括解码器、编码器和RPC请求处理器
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new rpcDecoder(rpcRequest.class)); // 解码器
                    pipeline.addLast(new rpcEncoder(rpcResponse.class)); // 编码器
                    pipeline.addLast(new RpcServerHandler(handlerMap)); // 处理 RPC 请求
                }
            });

            // 解析服务地址和端口号，并绑定到指定地址
            String[] addressArray = StringUtils.split(serviceAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            ChannelFuture future = serverBootstrap.bind(ip, port).sync();

            // 将服务注册到服务注册中心
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                    LOGGER.info("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            LOGGER.info("server started on port {}", port);

            // 等待服务器关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭线程组
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 重写 `setApplicationContext` 方法，用于在 Spring 应用上下文初始化时，扫描并注册所有带有 `@rpcService` 注解的 Bean。
     *
     * @param applicationContext Spring 应用上下文对象，用于获取容器中的 Bean。
     * @throws BeansException 如果在处理过程中发生 Bean 相关的异常，则抛出此异常。
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取所有带有 `@rpcService` 注解的 Bean
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(rpcService.class);

        // 如果存在带有 `@rpcService` 注解的 Bean
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            // 遍历所有带有 `@rpcService` 注解的 Bean
            for (Object serviceBean: serviceBeanMap.values()) {
                // 获取当前 Bean 的 `@rpcService` 注解
                rpcService rpcService = serviceBean.getClass().getAnnotation(rpcService.class);

                // 获取服务接口名称
                String serviceName = rpcService.interfaceName().getName();

                // 获取服务版本号
                String serviceVersion = rpcService.serviceVersion();

                // 如果版本号不为空，则将其拼接到服务名称中
                if(serviceVersion != null){
                    serviceVersion = serviceVersion.trim();
                    if(!StringUtils.isEmpty(serviceVersion)){
                        serviceName = serviceName + "-" + serviceVersion;
                    }
                }

                // 将服务名称和对应的 Bean 存入 handlerMap 中
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }
}

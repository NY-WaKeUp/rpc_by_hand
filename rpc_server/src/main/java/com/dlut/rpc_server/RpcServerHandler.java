package com.dlut.rpc_server;

import com.dlut.rpc_common.model.rpcRequest;
import com.dlut.rpc_common.model.rpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RpcServerHandler extends SimpleChannelInboundHandler<rpcRequest> {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    /**
     * 服务端接口实现类，存储服务名称与服务对象之间的映射关系
     */
    private final Map<String, Object> handlerMap;

    /**
     * RpcServerHandler 构造函数，用于初始化 RPC 服务器处理器。
     *
     * @param handlerMap 包含服务名称与对应服务实例的映射表。该映射表用于在处理 RPC 请求时查找并调用相应的服务实例。
     */
    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 处理接收到的RPC请求并返回响应。
     * 该方法会调用核心处理方法 `handle` 来处理请求，并将处理结果或异常封装到 `rpcResponse` 中，
     * 最后通过 `ChannelHandlerContext` 将响应写回客户端并关闭连接。
     *
     * @param ctx ChannelHandlerContext 对象，用于与客户端进行通信。
     * @param rpcRequest 接收到的RPC请求对象，包含请求的详细信息。
     * @throws Exception 如果在处理请求过程中发生异常，则抛出。
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, rpcRequest rpcRequest) throws Exception {
        rpcResponse rpcResponse = new rpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());

        try {
            // 调用核心处理方法处理请求，并将结果设置到响应对象中
            Object result = handle(rpcRequest);
            rpcResponse.setResult(result);
        } catch (Exception e) {
            // 如果处理过程中发生异常，记录日志并将异常信息设置到响应对象中
            LOGGER.error("handle result failure", e);
            rpcResponse.setException(e);
        }

        // 将响应写回客户端，并在写操作完成后关闭连接
        ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 当服务器捕获到异常时，该方法会被调用。
     * 该方法会记录异常信息并关闭与客户端的连接。
     *
     * @param ctx ChannelHandlerContext对象，表示与通道处理器相关的上下文信息。
     * @param cause Throwable对象，表示捕获到的异常。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 记录异常信息到日志中
        LOGGER.error("server caught exception", cause);

        // 关闭与客户端的连接
        ctx.close();
    }

    /**
     * 处理RPC请求并调用相应的方法。
     *
     * @param rpcRequest 包含RPC请求信息的对象，包括接口名称、服务版本、方法名、参数类型和参数值。
     * @return 调用目标方法后的返回值。
     * @throws NoSuchMethodException 如果找不到指定的方法。
     * @throws InvocationTargetException 如果目标方法调用时抛出异常。
     * @throws IllegalAccessException 如果无法访问目标方法。
     */
    private Object handle(rpcRequest rpcRequest) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获取服务名称和版本，并拼接完整的服务键
        String serviceName = rpcRequest.getInterfaceName();
        String serviceVersion = rpcRequest.getServiceVersion();
        if(serviceVersion != null){
            serviceVersion = serviceVersion.trim();
            if(!StringUtils.isEmpty(serviceVersion)){
                serviceName += "-" + serviceVersion;
            }
        }

        // 根据服务键获取对应的服务实例
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
        }

        // 获取目标方法并调用
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = rpcRequest.getMethodName();
        Object[] parameters = rpcRequest.getParameters();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        // 通过反射获取方法对象
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }
}

package com.dlut.rpc_client;

import com.dlut.rpc_common.enc_dec.rpcDecoder;
import com.dlut.rpc_common.enc_dec.rpcEncoder;
import com.dlut.rpc_common.model.rpcRequest;
import com.dlut.rpc_common.model.rpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class rpcClient extends SimpleChannelInboundHandler<rpcResponse> {

    /**
     * 日志
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(rpcClient.class);

    /**
     * 主机
     */

    private final String host;

    /**
     * 端口
     */

    private final int port;

    /**
     * RPC 响应
     */

    private rpcResponse response;

    /**
     * 构造函数
     *
     * @param host 主机
     * @param port 端口
     */

    public rpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 处理服务端发送过来的响应消息 RpcResponse
     *
     * @param channelHandlerContext
     * @param rpcResponse
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, rpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
    }


    /**
     * 当捕获到异常时调用此方法。记录错误日志并关闭与客户端的连接。
     *
     * @param ctx   ChannelHandlerContext对象，表示当前ChannelHandler的上下文，用于与ChannelPipeline进行交互。
     * @param cause Throwable对象，表示捕获到的异常或错误。
     * @throws Exception 如果在处理过程中发生异常，则抛出。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 记录错误日志，包含异常信息
        LOGGER.error("client catch exception", cause);

        // 关闭与客户端的连接
        ctx.close();
    }

    /**
     * 发送 RPC 请求并获取响应。
     * <p>
     * 该方法通过 Netty 框架建立与 RPC 服务器的连接，发送 RPC 请求，并等待服务器返回响应。
     * 在发送请求后，该方法会同步等待服务器响应，并在收到响应后关闭连接。
     *
     * @param rpcRequest 要发送的 RPC 请求对象，包含请求的具体数据。
     * @return rpcResponse RPC 服务器返回的响应对象，包含响应的具体数据。
     * @throws InterruptedException 如果线程在等待连接、发送请求或关闭连接时被中断，则抛出此异常。
     */
    public rpcResponse send(rpcRequest rpcRequest) throws InterruptedException {
        // 创建并初始化 Netty 的事件循环组，用于处理网络事件
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            // 配置 Netty 的 Bootstrap，设置事件循环组、通道类型和处理器
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    // 配置通道的管道，添加编码器、解码器和 RPC 响应处理器
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new rpcEncoder(rpcRequest.class)); // 编码器
                    pipeline.addLast(new rpcDecoder(rpcResponse.class)); // 解码器
                    pipeline.addLast(rpcClient.this); // 处理 RPC 响应
                }
            });
            // 设置 TCP 无延迟选项，减少网络延迟
            bootstrap.option(ChannelOption.TCP_NODELAY, true);

            // 连接到 RPC 服务器，并同步等待连接完成
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

            // 获取通道并写入 RPC 请求，同步等待请求发送完成
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(rpcRequest).sync();

            // 同步等待通道关闭，确保连接正常关闭
            channel.closeFuture().sync();

            // 返回 RPC 响应对象
            return response;
        } finally {
            // 优雅地关闭事件循环组，释放资源
            group.shutdownGracefully();
        }
    }
}



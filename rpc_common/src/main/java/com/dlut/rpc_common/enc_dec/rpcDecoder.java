package com.dlut.rpc_common.enc_dec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.dlut.rpc_common.serializer.rpcSerializer;

import java.util.List;

public class rpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass; // 反序列化成 genericClass 类型的对象

    public rpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    /**
     * 解码方法，用于将接收到的字节数据反序列化为对象。
     * 该方法会检查字节数据的完整性，并在数据不完整时等待更多数据到达。
     * 
     * @param channelHandlerContext 通道处理器上下文，用于处理通道相关操作
     * @param in 输入的字节缓冲区，包含待解码的字节数据
     * @param out 输出的对象列表，用于存储解码后的对象
     * @throws Exception 如果解码过程中发生异常
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字节数据来读取数据长度
        if (in.readableBytes() < 4) {
            return;
        }
        
        // 标记当前读取位置，以便在数据不完整时回退
        in.markReaderIndex();
        
        // 读取数据长度
        int dataLength = in.readInt();
        
        // 检查是否有足够的字节数据来读取完整的数据包
        if (in.readableBytes() < dataLength) {
            // 数据不完整，回退到标记位置并等待更多数据
            in.resetReaderIndex();
            return;
        }
        
        // 读取数据包内容
        byte[] body = new byte[dataLength];
        in.readBytes(body);
        
        // 反序列化字节数据为对象
        Object object = rpcSerializer.deserialize(body, genericClass);
        
        // 将反序列化后的对象添加到出站消息列表
        out.add(object);
    }

}

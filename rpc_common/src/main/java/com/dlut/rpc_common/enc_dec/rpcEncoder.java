package com.dlut.rpc_common.enc_dec;

import com.dlut.rpc_common.serializer.rpcSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class rpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass; // 待编码的对象类型

    public rpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    /**
     * 将传入的对象编码为字节流并写入到ByteBuf中。
     * 该方法首先检查传入的对象是否为指定泛型类的实例，如果是，则将其序列化为字节数组，
     * 并将字节数组的长度和数据依次写入到ByteBuf中。
     *
     * @param channelHandlerContext ChannelHandlerContext对象，用于处理通道事件
     * @param in                    需要编码的对象
     * @param out                   用于存储编码后数据的ByteBuf
     * @throws Exception 如果编码过程中发生异常
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
        // 检查传入的对象是否为指定泛型类的实例
        if (genericClass.isInstance(in)) {
            // 将对象序列化为字节数组
            byte[] data = rpcSerializer.serialize(in);
            // 将消息体长度写入消息头
            out.writeInt(data.length);
            // 将数据写入消息体
            out.writeBytes(data);
        }
    }
}

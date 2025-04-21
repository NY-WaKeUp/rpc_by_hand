package com.dlut.rpc_common.serializer;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ningyu
 * @description 自定义序列化/反序列化方法（基于 Protostuff）
 */
public class rpcSerializer {
    
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd(true); // 使用 Objenesis 来实例化对象，绕过构造函数

    /**
     * 根据给定的类对象获取对应的Schema对象。
     * 如果缓存中已经存在该类的Schema对象，则直接返回缓存中的对象；
     * 如果缓存中不存在，则通过RuntimeSchema.createFrom方法创建新的Schema对象，并将其放入缓存中。
     *
     * @param <T> 泛型类型，表示类的类型
     * @param cls 需要获取Schema的类对象
     * @return 返回与给定类对象对应的Schema对象
     */
    private static <T> Schema<T> getSchema(Class<T> cls) {
        // 从缓存中获取Schema对象
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        
        // 如果缓存中不存在，则创建新的Schema对象并放入缓存
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            cachedSchema.put(cls, schema);
        }
        
        // 返回Schema对象
        return schema;
    }

    /**
     * 将给定的对象序列化为字节数组。
     *
     * @param obj 需要序列化的对象
     * @param <T> 对象的类型
     * @return 返回序列化后的字节数组
     */
    public static <T> byte[] serialize(T obj) {
        // 获取对象的Class对象
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            // 获取对象的Schema对象
            Schema<T> schema = getSchema(cls);
            // 将对象序列化为字节数组
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear(); // 清空缓冲区
        }

    }
    /**
     * 将字节数组反序列化为对象。
     *
     * @param data 需要反序列化的字节数组
     * @param cls  目标对象的Class对象
     * @param <T>  对象的类型
     * @return 返回反序列化后的对象
     */
    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            // 创建一个新的对象实例
            T message = objenesis.newInstance(cls);
            // 获取对象的Schema对象
            Schema<T> schema = getSchema(cls);
            // 将字节数组反序列化为对象
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

    }
}

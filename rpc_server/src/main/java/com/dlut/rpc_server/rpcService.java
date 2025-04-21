package com.dlut.rpc_server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @rpcService 注解用于标识一个类或接口为 RPC 服务。
 * 该注解可以被 Spring 框架扫描，并且在运行时可以通过反射获取。
 *
 * @Target(ElementType.TYPE) 表示该注解可以应用于类、接口、枚举或注解上。
 * @Retention(RetentionPolicy.RUNTIME) 表示该注解在编译后的字节码中保留，并且在运行时可以通过反射获取。
 * @Component 表示该注解可以被 Spring 框架扫描并管理。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface rpcService {
    /**
     * 指定 RPC 服务所实现的接口类。
     *
     * @return 返回 RPC 服务所实现的接口类。
     */
    Class<?> interfaceName();

    /**
     * 指定 RPC 服务的版本号，默认为空字符串。
     *
     * @return 返回 RPC 服务的版本号。
     */
    String serviceVersion() default "";
}


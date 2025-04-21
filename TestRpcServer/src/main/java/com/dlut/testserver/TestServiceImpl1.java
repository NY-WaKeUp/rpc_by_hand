package com.dlut.testserver;

import com.dlut.rpc_server.rpcService;
import com.dlut.testserviceapi.testService;

/**
 * TestServiceImpl1 类实现了 testService 接口，提供了具体的服务实现。
 * 该类通过 @rpcService 注解标识为一个 RPC 服务，指定了接口名称和服务版本。
 *
 * @rpcService 注解参数说明：
 *   - interfaceName: 指定实现的接口为 testService.class
 *   - serviceVersion: 指定服务版本为 "1.0"
 */
@rpcService(interfaceName = testService.class)
public class TestServiceImpl1 implements testService {

    /**
     * hello 方法实现了 testService 接口中的 hello 方法，用于返回传入的字符串。
     *
     * @param name 传入的字符串参数，表示需要返回的内容
     * @return 返回传入的字符串 name
     */
    @Override
    public String hello(String name) {
        return name;
    }
}


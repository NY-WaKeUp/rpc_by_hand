package com.dlut.testserver;


import com.dlut.rpc_server.rpcService;
import com.dlut.testserviceapi.testService;
/**
 * TestServiceImpl2 类实现了 testService 接口，提供了具体的服务实现。
 * 该类通过 @rpcService 注解标识为 RPC 服务，指定了接口名称和服务版本。
 *
 * @rpcService 注解参数说明：
 *   - interfaceName: 指定实现的接口为 testService.class。
 *   - serviceVersion: 指定服务的版本为 "2.0"。
 */
@rpcService(interfaceName = testService.class, serviceVersion = "2.0")
public class TestServiceImpl2 implements testService {

    /**
     * hello 方法实现了 testService 接口中的 hello 方法。
     * 该方法接收一个字符串参数 name，并返回相同的字符串。
     *
     * @param name 输入的字符串参数，表示用户的名字。
     * @return 返回与输入相同的字符串，即用户的名字。
     */
    @Override
    public String hello(String name) {
        return name;
    }

}

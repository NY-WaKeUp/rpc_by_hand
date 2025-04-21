package com.dlut.testserviceapi;
/**
 * 测试服务接口。
 */
public interface testService {

    /**
     * 根据传入的名字返回问候语。
     *
     * @param name
     * @return 字符串
     */
    String hello(String name);
}

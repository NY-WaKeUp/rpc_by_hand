<h1 style="text-align:center;">rpc_by_hand</h1>

## Overview
**rpc_by_hand** 是一个使用 `netty`、`zookeeper` 和 `protobuf` 实现的简易RPC框架。
主要是为了让自己熟悉RPC的基本原理和实现，代码中有详细的注释，适合学习和参考。

## Modules
- `rpc_common`：包含封装 RPC 请求与响应的实体类 `model`，Netty 编解码器 `enc_dec` 以及序列化/反序列 `serializer`
- `rpc_server`：Netty / RPC 服务端，包括处理并响应客户端的请求，服务注册与发现，服务端的启动和关闭等
- `rpc_client`：Netty / RPC 客户端，包括向服务端发送请求，接收服务端的响应
- `rpc_registry`：定义进行服务注册与发现的接口
- `rpc_registry_zookeeper`：基于 `zookeeper` 实现服务的注册与发现，负载均衡
- `TestRpcClient`：作为客户端，测试rpc服务，使用动态代理调用远程方法
- `TestRpcServer`：实现服务的接口，启动并发布rpc服务
- `TestServiceApi`：定义服务接口（RPC 接口）

## Features
- [x] 使用 Spring 提供依赖注入与参数配置
- [ ] 集成 `Spring` 通过注解实现RPC服务的发布和调用
- [x] 使用`Netty`高效地进行网络传输
  - [ ] 进一步基于开源的序列化框架 `Protostuff` 让用户可以通过配置文件指定序列化方式，避免硬编码
  - [x] 自定义编解码器
  - [ ] TCP 心跳机制，自定义应用层的 `Netty` 心跳机制
  - [x] 使用 `JDK/CGLIB` 动态代理机制调用远程方法
- [x] 借助 `Zookeeper`容器实现服务注册和发现
    - [x] 客户端调用远程服务的时候进行负载均衡，目前使用的策略为随机负载均衡，也就是随机选取一个服务地址
    - [ ] 支持多种负载均衡策略


package com.dlut.rpc_common.model;

/**
 * @author ningyu
 * @description rpc请求类，包含请求的唯一标识符、接口名称、服务版本、方法名称、参数类型和参数值等信息
 */
public class rpcRequest {
    private String requestId; // 请求的唯一标识符
    private String interfaceName; // 接口名称
    private String serviceVersion; // 服务版本
    private String methodName; // 方法名称
    private Class<?>[] parameterTypes; // 参数类型
    private Object[] parameters; // 方法参数
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String className) {
        this.interfaceName = className;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }


}

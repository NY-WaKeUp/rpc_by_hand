package com.dlut.rpc_common.model;

/**
 * @author ningyu
 * @description rpc响应类,包含请求的唯一标识符、方法返回值和异常信息等
 */
public class rpcResponse {

    /**
     * 请求的唯一标识符
     */

    private String requestId;

    /**
     * 方法返回值
     */

    private Object result;

    /**
     * 异常信息
     */

    private Exception exception;
    /**
     * 判断是否有异常
     *
     * @return true if there is an exception, false otherwise
     */
    public boolean hasException() {
        return exception != null;
    }
    /**
     * 获取请求的唯一标识符
     *
     * @return the request ID
     */
    public String getRequestId() {
        return requestId;
    }
    /**
     * 设置请求的唯一标识符
     *
     * @param requestId the request ID
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    /**
     * 获取方法返回值
     *
     * @return the result
     */
    public Object getResult() {
        return result;
    }
    /**
     * 设置方法返回值
     *
     * @param result the result
     */
    public void setResult(Object result) {
        this.result = result;
    }
    /**
     * 获取异常信息
     *
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }
    /**
     * 设置异常信息
     *
     * @param exception the exception
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}

package cn.daydayup.dev.streamable.mcp.starter.core;

import javax.servlet.http.HttpServletRequest;

/**
 * MCP请求上下文持有者
 * 用于在工具方法中获取当前的HttpServletRequest
 */
public class McpRequestContextHolder {
    
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();
    
    /**
     * 设置当前请求
     * @param request HttpServletRequest
     */
    public static void setRequest(HttpServletRequest request) {
        requestHolder.set(request);
    }
    
    /**
     * 获取当前请求
     * @return HttpServletRequest
     */
    public static HttpServletRequest getCurrentRequest() {
        return requestHolder.get();
    }
    
    /**
     * 清除当前请求上下文
     */
    public static void clear() {
        requestHolder.remove();
    }
}
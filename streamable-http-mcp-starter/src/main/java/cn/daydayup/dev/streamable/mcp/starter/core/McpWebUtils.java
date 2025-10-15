package cn.daydayup.dev.streamable.mcp.starter.core;

import javax.servlet.http.HttpServletRequest;

/**
 * MCP Web工具类
 * 提供便捷方法访问当前HTTP请求上下文
 */
public class McpWebUtils {
    
    /**
     * 获取当前HTTP请求
     * 
     * @return HttpServletRequest 当前HTTP请求对象
     */
    public static HttpServletRequest getCurrentRequest() {
        return McpRequestContextHolder.getCurrentRequest();
    }
    
    /**
     * 获取请求头信息
     * 
     * @param headerName 头名称
     * @return String 头信息值
     */
    public static String getRequestHeader(String headerName) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader(headerName);
        }
        return null;
    }
    
    /**
     * 获取请求参数
     * 
     * @param paramName 参数名称
     * @return String 参数值
     */
    public static String getRequestParam(String paramName) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getParameter(paramName);
        }
        return null;
    }
    
    /**
     * 获取客户端IP地址
     * 
     * @return String 客户端IP地址
     */
    public static String getClientIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }
            
            return request.getRemoteAddr();
        }
        return null;
    }
}
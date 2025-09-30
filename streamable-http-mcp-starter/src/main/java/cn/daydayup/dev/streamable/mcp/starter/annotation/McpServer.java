package cn.daydayup.dev.streamable.mcp.starter.annotation;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * MCP服务配置注解
 * 用于配置MCP服务的元数据
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
public @interface McpServer {
    
    /**
     * 服务器名称
     */
    String serverName() default "MCP Server";
    
    /**
     * 服务器版本
     */
    String serverVersion() default "1.0.0";
    
    /**
     * 协议版本
     */
    String protocolVersion() default "2024-11-05";
}
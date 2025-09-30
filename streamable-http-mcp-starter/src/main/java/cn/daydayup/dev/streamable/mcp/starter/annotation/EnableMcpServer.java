package cn.daydayup.dev.streamable.mcp.starter.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * MCP服务端点注解
 * 用于标记一个类作为MCP服务端点
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
@Import(cn.daydayup.dev.streamable.mcp.starter.config.McpAutoConfiguration.class)
public @interface EnableMcpServer {
    
    /**
     * Alias for {@link RequestMapping#path}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String path() default "";
    
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
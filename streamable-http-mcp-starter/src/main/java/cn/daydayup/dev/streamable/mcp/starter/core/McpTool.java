package cn.daydayup.dev.streamable.mcp.starter.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个方法作为MCP工具
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpTool {
    
    /**
     * 工具名称
     */
    String name() default "";
    
    /**
     * 工具描述
     */
    String description() default "";
}
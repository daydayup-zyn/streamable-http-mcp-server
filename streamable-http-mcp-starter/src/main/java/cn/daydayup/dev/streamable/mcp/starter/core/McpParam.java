package cn.daydayup.dev.streamable.mcp.starter.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MCP工具参数注解
 * 用于描述MCP工具方法的参数信息
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpParam {
    
    /**
     * 参数名称
     */
    String name() default "";
    
    /**
     * 参数描述
     */
    String description() default "";
    
    /**
     * 是否必填
     */
    boolean required() default false;
}
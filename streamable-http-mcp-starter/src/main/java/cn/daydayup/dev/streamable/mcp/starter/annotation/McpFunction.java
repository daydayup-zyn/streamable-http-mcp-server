package cn.daydayup.dev.streamable.mcp.starter.annotation;

import java.lang.annotation.*;

/**
 * @ClassName McpFunction
 * @Description MCP方法注解
 * @Author ZhaoYanNing
 * @Date 2025/9/30 13:39
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface McpFunction {

    /**
     * 函数名称
     */
    String name();

    /**
     * 函数描述
     */
    String description();
}

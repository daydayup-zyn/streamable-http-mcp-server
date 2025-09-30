package cn.daydayup.dev.streamable.mcp.starter.annotation;

import java.lang.annotation.*;

/**
 * @ClassName McpParam
 * @Description MCP方法参数注解
 * @Author ZhaoYanNing
 * @Date 2025/9/30 13:40
 * @Version 1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface McpParam {

    /**
     * 参数名称
     */
    String name();

    /**
     * 参数描述
     */
    String description();

    /**
     * 参数可选值枚举
     */
    String[] enums() default {};

    /**
     * 参数是否必需
     */
    boolean required() default false;
}

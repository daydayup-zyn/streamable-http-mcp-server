package cn.daydayup.dev.streamable.mcp.starter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @ClassName McpServerEndpoint
 * @Description MCP服务端接口
 * @Author ZhaoYanNing
 * @Date 2025/9/30 13:40
 * @Version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface McpServerEndpoint {

    /*
     * MCP服务接口地址
     */
    String path();

    /*
     * MCP服务名称
     */
    String name() default "";

    /*
     * MCP服务版本
     */
    String version() default "";
}

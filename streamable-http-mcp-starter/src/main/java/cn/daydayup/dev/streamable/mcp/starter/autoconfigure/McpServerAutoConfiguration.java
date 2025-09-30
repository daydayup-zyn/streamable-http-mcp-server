package cn.daydayup.dev.streamable.mcp.starter.autoconfigure;

import cn.daydayup.dev.streamable.mcp.starter.core.McpServerEndpointAutoRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName McpServerAutoConfiguration
 * @Description 自动配置
 * @Author ZhaoYanNing
 * @Date 2025/9/30 15:11
 * @Version 1.0
 */
@Configuration
public class McpServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public McpServerEndpointAutoRegistry mcpServerEndpointAutoRegistry() {
        return new McpServerEndpointAutoRegistry();
    }
}
package cn.daydayup.dev.streamable.mcp.starter.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MCP服务自动配置入口
 */
@Configuration
@Import(McpStarterAutoConfiguration.class)
public class McpServerAutoConfiguration {
}
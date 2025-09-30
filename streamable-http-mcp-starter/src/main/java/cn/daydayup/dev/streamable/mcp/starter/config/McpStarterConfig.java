package cn.daydayup.dev.streamable.mcp.starter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cn.daydayup.dev.streamable.mcp.starter.autoconfigure.McpServerAutoConfiguration;

/**
 * 主配置类，用于导入自动配置
 */
@Configuration
@Import(McpServerAutoConfiguration.class)
public class McpStarterConfig {
}
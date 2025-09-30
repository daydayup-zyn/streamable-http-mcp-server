package cn.daydayup.dev.streamable.mcp.starter.core;

import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * MCP生命周期管理器
 */
@Configuration
public class McpLifecycleManager implements SmartLifecycle {
    
    @Resource
    private McpToolScanner toolScanner;
    
    @Resource
    private McpToolRegistry toolRegistry;
    
    private boolean running = false;
    
    @PostConstruct
    public void init() {
        toolScanner.setToolRegistry(toolRegistry);
    }
    
    @Override
    public void start() {
        if (!running) {
            toolScanner.scanAndRegisterTools();
            running = true;
        }
    }
    
    @Override
    public void stop() {
        running = false;
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @PreDestroy
    public void destroy() {
        stop();
    }
}
package cn.daydayup.dev.streamable.mcp.starter.core;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MCP工具注册表
 */
@Component
public class McpToolRegistry {
    
    private final Map<String, McpToolDefinition> tools = new ConcurrentHashMap<>();
    private final Map<String, Object> toolInstances = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 注册工具
     */
    public void registerTool(String name, String description, Object instance, Method method, Map<String, Object> inputSchema) {
        McpToolDefinition tool = new McpToolDefinition(name, description, instance, method, inputSchema);
        tools.put(name, tool);
        toolInstances.put(name, instance);
    }
    
    /**
     * 调用工具
     */
    public Object callTool(String toolName, Map<String, Object> arguments) {
        return callTool(toolName, arguments, null);
    }
    
    /**
     * 调用工具，支持指定控制器实例
     */
    public Object callTool(String toolName, Map<String, Object> arguments, Object controllerInstance) {
        McpToolDefinition tool = tools.get(toolName);
        if (tool == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            List<Map<String, Object>> content = new ArrayList<>();
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Tool not found: " + toolName);
            content.add(textContent);
            errorResponse.put("content", content);
            errorResponse.put("isError", true);
            return errorResponse;
        }
        
        try {
            // 如果提供了控制器实例，则使用该实例调用方法
            if (controllerInstance != null) {
                return tool.invoke(arguments, controllerInstance);
            }
            return tool.invoke(arguments);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            List<Map<String, Object>> content = new ArrayList<>();
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Error calling tool: " + e.getMessage());
            content.add(textContent);
            errorResponse.put("content", content);
            errorResponse.put("isError", true);
            return errorResponse;
        }
    }
    
    /**
     * 获取所有工具的JSON表示
     */
    public ArrayNode getToolsAsJson() {
        ArrayNode toolsArray = objectMapper.createArrayNode();
        for (McpToolDefinition tool : tools.values()) {
            ObjectNode toolNode = objectMapper.createObjectNode();
            toolNode.put("name", tool.getName());
            toolNode.put("description", tool.getDescription());
            if (tool.getInputSchema() != null) {
                toolNode.set("inputSchema", objectMapper.valueToTree(tool.getInputSchema()));
            }
            toolsArray.add(toolNode);
        }
        return toolsArray;
    }
    
    /**
     * 获取所有已注册的工具定义
     */
    public Map<String, McpToolDefinition> getTools() {
        return new HashMap<>(tools);
    }
}
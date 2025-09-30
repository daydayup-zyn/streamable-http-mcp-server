package cn.daydayup.dev.streamable.mcp.starter.core;

import cn.daydayup.dev.streamable.mcp.starter.annotation.EnableMcpServer;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动扫描并注册MCP工具
 */
@Component
public class McpToolScanner implements ApplicationContextAware {
    
    private ApplicationContext applicationContext;
    private McpToolRegistry toolRegistry;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    public void setToolRegistry(McpToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }
    
    /**
     * 扫描并注册所有标记了@McpTool注解的方法
     */
    public void scanAndRegisterTools() {
        if (toolRegistry == null) {
            throw new IllegalStateException("McpToolRegistry must be set before scanning");
        }
        
        // 获取所有Spring管理的Bean
        Map<String, Object> beans = applicationContext.getBeansOfType(Object.class);
        
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();
            Class<?> clazz = bean.getClass();
            
            // 检查类是否使用了@EnableMcpServer注解或者@McpServer注解
            if (clazz.isAnnotationPresent(EnableMcpServer.class) || clazz.isAnnotationPresent(McpServer.class)) {
                // 遍历所有方法查找@McpTool注解
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(McpTool.class)) {
                        McpTool toolAnnotation = method.getAnnotation(McpTool.class);
                        
                        String toolName = toolAnnotation.name();
                        if (toolName == null || toolName.trim().isEmpty()) {
                            toolName = method.getName();
                        }
                        
                        String description = toolAnnotation.description();
                        
                        // 创建输入模式（基于参数注解）
                        Map<String, Object> inputSchema = createInputSchema(method);
                        
                        // 注册工具
                        toolRegistry.registerTool(toolName, description, bean, method, inputSchema);
                    }
                }
            }
        }
    }
    
    private Map<String, Object> createInputSchema(Method method) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        List<String> requiredList = new ArrayList<>();
        
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Map<String, Object> prop = new HashMap<>();
            
            Class<?> paramType = param.getType();
            if (paramType.equals(String.class)) {
                prop.put("type", "string");
            } else if (paramType.equals(Integer.class) || paramType.equals(int.class)) {
                prop.put("type", "integer");
            } else if (paramType.equals(Long.class) || paramType.equals(long.class)) {
                prop.put("type", "integer");
            } else if (paramType.equals(Double.class) || paramType.equals(double.class)) {
                prop.put("type", "number");
            } else if (paramType.equals(Boolean.class) || paramType.equals(boolean.class)) {
                prop.put("type", "boolean");
            } else if (paramType.equals(Map.class)) {
                prop.put("type", "object");
            } else {
                prop.put("type", "string");
            }
            
            String paramName = param.getName(); // 默认参数名
            boolean required = false;
            
            // 检查是否有@McpParam注解
            if (param.isAnnotationPresent(McpParam.class)) {
                McpParam paramAnnotation = param.getAnnotation(McpParam.class);
                if (!paramAnnotation.name().isEmpty()) {
                    paramName = paramAnnotation.name();
                }
                
                if (!paramAnnotation.description().isEmpty()) {
                    prop.put("description", paramAnnotation.description());
                }
                
                required = paramAnnotation.required();
            }
            
            properties.put(paramName, prop);
            
            if (required) {
                requiredList.add(paramName);
            }
        }
        
        schema.put("properties", properties);
        schema.put("required", requiredList);
        
        return schema;
    }
}
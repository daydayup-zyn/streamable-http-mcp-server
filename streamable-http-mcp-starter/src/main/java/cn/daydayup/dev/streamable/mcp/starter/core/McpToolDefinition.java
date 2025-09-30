package cn.daydayup.dev.streamable.mcp.starter.core;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * MCP工具定义
 */
public class McpToolDefinition {
    
    private final String name;
    private final String description;
    private final Object instance;
    private final Method method;
    private final Map<String, Object> inputSchema;
    
    public McpToolDefinition(String name, String description, Object instance, Method method, Map<String, Object> inputSchema) {
        this.name = name;
        this.description = description;
        this.instance = instance;
        this.method = method;
        this.inputSchema = inputSchema;
        
        // 确保方法是可访问的
        method.setAccessible(true);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Map<String, Object> getInputSchema() {
        return inputSchema;
    }
    
    public Object invoke(Map<String, Object> arguments) throws Exception {
        return invoke(arguments, null);
    }
    
    public Object invoke(Map<String, Object> arguments, Object controllerInstance) throws Exception {
        Object targetInstance = controllerInstance != null ? controllerInstance : instance;
        
        // 根据方法参数数量和类型调用方法
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            return method.invoke(targetInstance);
        } else if (paramTypes.length == 1 && paramTypes[0].equals(Map.class)) {
            return method.invoke(targetInstance, arguments);
        } else {
            // 尝试按名称匹配参数
            Object[] args = new Object[paramTypes.length];
            String[] parameterNames = getParameterNames(method);
            
            for (int i = 0; i < paramTypes.length; i++) {
                if (i < parameterNames.length) {
                    args[i] = convertValue(arguments.get(parameterNames[i]), paramTypes[i]);
                } else {
                    args[i] = getDefaultInstance(paramTypes[i]);
                }
            }
            
            return method.invoke(targetInstance, args);
        }
    }
    
    private String[] getParameterNames(Method method) {
        // 简化处理，实际项目中可能需要使用ASM库或其他方式获取真实参数名
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            names[i] = "arg" + i;
        }
        return names;
    }
    
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return getDefaultInstance(targetType);
        }
        
        if (targetType.isInstance(value)) {
            return value;
        }
        
        // 添加常见类型的转换
        if (targetType.equals(String.class)) {
            return String.valueOf(value);
        } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(String.valueOf(value));
        } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(String.valueOf(value));
        } else if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(String.valueOf(value));
        } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            if (value instanceof Boolean) {
                return value;
            }
            return Boolean.parseBoolean(String.valueOf(value));
        }
        
        return value;
    }
    
    private Object getDefaultInstance(Class<?> type) {
        if (type.equals(boolean.class)) return false;
        if (type.equals(byte.class)) return (byte) 0;
        if (type.equals(short.class)) return (short) 0;
        if (type.equals(int.class)) return 0;
        if (type.equals(long.class)) return 0L;
        if (type.equals(float.class)) return 0.0f;
        if (type.equals(double.class)) return 0.0d;
        if (type.equals(char.class)) return '\0';
        return null;
    }
}
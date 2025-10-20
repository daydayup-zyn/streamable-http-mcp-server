package cn.daydayup.dev.streamable.mcp.starter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.MethodNotAllowedException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName McpStreamableHttpHandler
 * @Description MCP HTTP接口处理器
 * @Author ZhaoYanNing
 * @Date 2025/9/30 16:22
 * @Version 1.0
 */
@Data
@Slf4j
public class McpStreamableHttpHandler {

    private String name;
    private String version;
    private List<McpFunctionInfo> functionInfos = new ArrayList<>();
    private Object targetBean;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<ObjectNode> handleGet() {
        throw new MethodNotAllowedException(HttpMethod.GET, null);
    }

    public ResponseEntity<ObjectNode> handlePost(@RequestBody String body, HttpServletRequest request) throws Exception {
        try {
            // 设置当前请求上下文
            McpRequestContextHolder.setRequest(request);
            
            ObjectNode requestNode = objectMapper.readValue(body, ObjectNode.class);
            if (requestNode == null || !requestNode.has("id")) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
            }
            String id = requestNode.get("id").asText();
            String method = requestNode.get("method").asText();

            switch (method) {
                case "initialize":
                    return handleInitialize(id);
                case "tools/list":
                    return handleListTools(id);
                case "tools/call":
                    return handleCallTool(requestNode);
                case "ping":
                    return handlePing(id);
                default:
                    return handleUnsupportedMethod(id, method);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            // 清除请求上下文
            McpRequestContextHolder.clear();
        }
    }

    /**
     * 处理初始化请求
     *
     * @param id 请求ID
     * @return 响应实体
     */
    private ResponseEntity<ObjectNode> handleInitialize(String id) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);

        ObjectNode result = response.putObject("result");
        result.put("protocolVersion", "2024-11-05");
        ObjectNode capabilities = result.putObject("capabilities");

        ObjectNode serverInfo = result.putObject("serverInfo");
        serverInfo.put("name", name);
        serverInfo.put("version", version);
        return ResponseEntity.ok(response);
    }

    /**
     * 处理未知方法请求
     *
     * @param id     请求ID
     * @param method 请求方法
     * @return 响应实体
     */
    private ResponseEntity<ObjectNode> handleUnsupportedMethod(String id, String method) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);

        ObjectNode error = response.putObject("error");
        error.put("code", -32601);
        error.put("message", "本服务器不支持 " + method + " 方法");

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理工具列表请求
     *
     * @param id 请求ID
     * @return 响应实体
     */
    private ResponseEntity<ObjectNode> handleListTools(String id) {
        log.info("获取工具列表, 请求ID: {}", id);
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);

        ObjectNode result = response.putObject("result");
        result.putArray("tools");

        // 将functionInfos转换为工具列表
        if (functionInfos != null && !functionInfos.isEmpty()) {
            for (McpFunctionInfo functionInfo : functionInfos) {
                ObjectNode tool = result.withArray("tools").addObject();
                tool.put("name", functionInfo.getName());
                tool.put("description", functionInfo.getDescription());

                // 添加必需参数列表
                if (functionInfo.getParams() != null && !functionInfo.getParams().isEmpty()) {
                    // 添加参数信息
                    ObjectNode paramsNode = tool.putObject("inputSchema");
                    paramsNode.put("type", "object");
                    ObjectNode properties = paramsNode.putObject("properties");
                    List<String> required = new ArrayList<>();

                    for (McpFunctionInfo.ParamInfo paramInfo : functionInfo.getParams()) {
                        if (paramInfo == null) {
                            continue;
                        }
                        ObjectNode paramNode = properties.putObject(paramInfo.getName());
                        paramNode.put("type", getJsonSchemaType(paramInfo.getType()));
                        paramNode.put("description", paramInfo.getDescription());

                        // 如果有枚举值，添加枚举值
                        if (paramInfo.getEnums() != null) {
                            for (String enumValue : paramInfo.getEnums()) {
                                paramNode.withArray("enum").add(enumValue);
                            }
                        }
                        // 如果是必需参数，添加到必需参数列表
                        if (paramInfo.isRequired()) {
                            required.add(paramInfo.getName());
                        }
                    }
                    // 添加必需参数列表
                    if (!required.isEmpty()) {
                        for (String req : required) {
                            paramsNode.withArray("required").add(req);
                        }
                    }
                }
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 处理工具调用请求
     *
     * @param request 请求实体
     * @return 响应实体
     */
    private ResponseEntity<ObjectNode> handleCallTool(ObjectNode request) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", request.get("id").asText());

        // 获取工具名称和参数
        String toolName = null;
        ObjectNode params = null;

        if (request.has("params") && request.get("params").has("name")) {
            toolName = request.get("params").get("name").asText();

            if (request.get("params").has("arguments")) {
                params = (ObjectNode) request.get("params").get("arguments");
            }
        }

        // 查找对应的函数
        McpFunctionInfo targetFunction = null;
        for (McpFunctionInfo functionInfo : functionInfos) {
            if (functionInfo.getName().equals(toolName)) {
                targetFunction = functionInfo;
                break;
            }
        }

        // 如果找到了函数，尝试调用
        if (targetFunction != null && targetBean != null) {
            try {
                Method method = targetFunction.getMethod();
                Parameter[] parameters = method.getParameters();
                Object[] args = new Object[parameters.length];

                // 准备参数
                for (int i = 0; i < parameters.length; i++) {
                    McpFunctionInfo.ParamInfo paramInfo = targetFunction.getParams().get(i);
                    String paramName = paramInfo.getName();
                    Class<?> paramType = parameters[i].getType();

                    if (params != null && params.has(paramName)) {
                        args[i] = convertParamValue(params.get(paramName).asText(), paramType);
                    } else if (paramInfo.isRequired()) {
                        // 必需参数缺失
                        log.warn("缺少必需参数: {}", paramName);
                        ObjectNode error = response.putObject("error");
                        error.put("code", -32602);
                        error.put("message", "缺少必需参数: " + paramName);
                        return ResponseEntity.ok(response);
                    }
                }

                // 调用方法
                Object result = method.invoke(targetBean, args);

                // 构建响应
                ObjectNode resultNode = response.putObject("result");

                List<Map<String, Object>> content = new ArrayList<>();
                Map<String, Object> textContent = new HashMap<>();
                textContent.put("type", "text");
                textContent.put("text", result != null ? result.toString() : "");
                content.add(textContent);

                resultNode.set("content", objectMapper.valueToTree(content));

            } catch (Exception e) {
                // 处理其他异常
                log.error("工具 {} 执行时发生异常", toolName, e);
                ObjectNode error = response.putObject("error");
                error.put("code", -32603);
                error.put("message", "工具 " + toolName + " 执行时发生异常" + e.getMessage());
            }
        } else {
            // 如果没有找到函数，返回错误
            log.warn("找不到指定的工具: {}", toolName);
            ObjectNode error = response.putObject("error");
            error.put("code", -32601);
            error.put("message", "找不到指定的工具: " + toolName);
        }

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ObjectNode> handlePing(String id) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        response.put("result",objectMapper.createObjectNode());

        return ResponseEntity.ok(response);
    }

    /**
     * 根据参数类型获取对应的JSON Schema类型
     *
     * @param type 参数类型
     * @return JSON Schema类型字符串
     */
    private String getJsonSchemaType(Class<?> type) {
        if (String.class.equals(type)) {
            return "string";
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            return "double";
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            return "float";
        } else if (Integer.class.equals(type) || int.class.equals(type)) {
            return "integer";
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            return "long";
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            return "boolean";
        } else {
            // 默认返回string类型
            return "string";
        }
    }

    /**
     * 根据目标参数类型转换参数值
     *
     * @param value 参数值（字符串形式）
     * @param targetType 目标类型
     * @return 转换后的参数值
     */
    private Object convertParamValue(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        try {
            if (String.class.equals(targetType)) {
                return value;
            } else if (Double.class.equals(targetType) || double.class.equals(targetType)) {
                return Double.parseDouble(value);
            } else if (Float.class.equals(targetType) || float.class.equals(targetType)) {
                return Float.parseFloat(value);
            } else if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
                return Integer.parseInt(value);
            } else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
                return Long.parseLong(value);
            } else if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
                return Boolean.parseBoolean(value);
            } else {
                // 默认返回原始字符串
                return value;
            }
        } catch (NumberFormatException e) {
            // 类型转换失败时返回原始字符串值
            log.warn("参数类型转换失败: value={}, targetType={}", value, targetType.getSimpleName(), e);
            return value;
        }
    }
}

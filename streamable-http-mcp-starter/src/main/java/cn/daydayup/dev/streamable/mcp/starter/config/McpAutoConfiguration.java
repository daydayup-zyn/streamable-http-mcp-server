package cn.daydayup.dev.streamable.mcp.starter.config;

import cn.daydayup.dev.streamable.mcp.starter.annotation.EnableMcpServer;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpServer;
import cn.daydayup.dev.streamable.mcp.starter.core.McpController;
import cn.daydayup.dev.streamable.mcp.starter.core.McpToolRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * MCP自动配置类
 */
public class McpAutoConfiguration implements ImportBeanDefinitionRegistrar {
    
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 检查是否使用了@EnableMcpServer注解（保持向后兼容）
        if (importingClassMetadata.hasAnnotation(EnableMcpServer.class.getName())) {
            // 获取注解属性
            Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableMcpServer.class.getName());
            
            // 创建动态MCP控制器
            createMcpController(registry, attributes, importingClassMetadata.getClassName());
        }
        // 检查是否使用了@McpServer注解（新的使用方式）
        else if (importingClassMetadata.hasAnnotation(McpServer.class.getName())) {
            // 获取注解属性
            Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(McpServer.class.getName());
            
            // 为继承了McpController的类设置属性
            configureMcpController(attributes, importingClassMetadata.getClassName());
        }
    }
    
    private void createMcpController(BeanDefinitionRegistry registry, Map<String, Object> attributes, String userControllerClassName) {
        // 创建动态控制器Bean定义
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        
        // 设置构造函数参数
        String userControllerBeanName = getUserControllerBeanName(userControllerClassName);
        attributes.put("userControllerBeanName", userControllerBeanName);

        DynamicMcpController dynamicMcpController = new DynamicMcpController(attributes);
        beanDefinition.setBeanClass(dynamicMcpController.getClass());
        
        // 注册Bean
        registry.registerBeanDefinition(userControllerBeanName, beanDefinition);
    }
    
    private void configureMcpController(Map<String, Object> attributes, String userControllerClassName) {
        // 这里我们不需要创建新的控制器，因为用户已经通过继承McpController创建了控制器
        // 我们只需要确保McpToolScanner能够扫描到这些工具即可
    }
    
    private String getUserControllerBeanName(String className) {
        // 从完整类名中提取简单类名
        String simpleName = className;
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < className.length() - 1) {
            simpleName = className.substring(lastDotIndex + 1);
        }
        
        // 将类名转换为Bean名称 (首字母小写)
        if (simpleName.isEmpty()) {
            return simpleName;
        }
        
        StringBuilder beanName = new StringBuilder(simpleName);
        beanName.setCharAt(0, Character.toLowerCase(beanName.charAt(0)));
        return beanName.toString();
    }
    
    /**
     * 动态MCP控制器类
     */
    @RestController
    @RequestMapping
    public static class DynamicMcpController {
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final String serverName;
        private final String serverVersion;
        private final String protocolVersion;
        private final String userControllerBeanName;

        public DynamicMcpController(Map<String, Object> attributes) {
            this.serverName = (String) attributes.get("serverName");;
            this.serverVersion = (String) attributes.get("serverVersion");;
            this.protocolVersion = (String) attributes.get("protocolVersion");;
            this.userControllerBeanName = (String) attributes.get("userControllerBeanName");
        }

        @GetMapping
        public void handleGet() {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "GET method not supported");
        }
        
        @PostMapping
        public ResponseEntity<ObjectNode> handlePost(@RequestBody String body) throws Exception {
            // 从Spring容器获取用户控制器实例
            McpToolRegistry toolRegistry = SpringContextUtil.getBean(McpToolRegistry.class);
            Object userController = SpringContextUtil.getBean(userControllerBeanName);
            
            JsonNode request = objectMapper.readTree(body);
            ResponseEntity<ObjectNode> response = null;
            
            String id = request.has("id") ? request.get("id").asText() : null;
            
            if (id == null) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
            } else {
                String method = request.get("method").asText();
                switch (method) {
                    case "initialize":
                        response = handleInitialize(id);
                        break;
                    case "tools/list":
                        response = handleListTools(id, toolRegistry);
                        break;
                    case "tools/call":
                        response = handleCallTool(request, toolRegistry, userController);
                        break;
                    case "ping":
                        response = handlePing(id);
                        break;
                    default:
                        response = handleUnsupportedMethod(id, method);
                        break;
                }
            }
            
            return response;
        }
        
        private ResponseEntity<ObjectNode> handleInitialize(String id) {
            ObjectNode response = objectMapper.createObjectNode();
            
            response.put("jsonrpc", "2.0");
            response.put("id", id);
            
            ObjectNode result = response.putObject("result");
            result.put("protocolVersion", protocolVersion);
            
            ObjectNode capabilities = result.putObject("capabilities");
            // 可以根据需要添加能力
            
            ObjectNode serverInfo = result.putObject("serverInfo");
            serverInfo.put("name", serverName);
            serverInfo.put("version", serverVersion);
            
            return ResponseEntity.ok(response);
        }
        
        private ResponseEntity<ObjectNode> handleListTools(String id, McpToolRegistry toolRegistry) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.put("id", id);
            
            ObjectNode result = response.putObject("result");
            result.set("tools", toolRegistry.getToolsAsJson());
            
            return ResponseEntity.ok(response);
        }
        
        private ResponseEntity<ObjectNode> handleCallTool(JsonNode request, McpToolRegistry toolRegistry, Object userController) throws Exception {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.put("id", request.get("id").asText());
            
            String toolName = request.get("params").get("name").asText();
            JsonNode argumentsNode = request.get("params").get("arguments");
            
            Map<String, Object> arguments = new HashMap<>();
            if (argumentsNode != null && !argumentsNode.isNull()) {
                arguments = objectMapper.convertValue(argumentsNode, Map.class);
            }
            
            Object result = toolRegistry.callTool(toolName, arguments, userController);
            response.set("result", objectMapper.valueToTree(result));
            
            return ResponseEntity.ok(response);
        }
        
        private ResponseEntity<ObjectNode> handlePing(String id) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.put("id", id);
            response.set("result", objectMapper.createObjectNode());
            
            return ResponseEntity.ok(response);
        }
        
        private ResponseEntity<ObjectNode> handleUnsupportedMethod(String id, String method) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.put("id", id);
            
            ObjectNode error = response.putObject("error");
            error.put("code", -32601);
            error.put("message", "Method not supported: " + method);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
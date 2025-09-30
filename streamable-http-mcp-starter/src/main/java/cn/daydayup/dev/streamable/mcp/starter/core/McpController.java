package cn.daydayup.dev.streamable.mcp.starter.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * MCP控制器基类
 */
@RestController
public abstract class McpController {
    
    protected final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    protected McpToolRegistry toolRegistry;
    
    protected String serverName = "MCP Server";
    protected String serverVersion = "1.0.0";
    protected String protocolVersion = "2024-11-05";
    
    @GetMapping
    public void handleGet() {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "GET method not supported");
    }
    
    @PostMapping
    public ResponseEntity<ObjectNode> handlePost(@RequestBody String body) throws Exception {
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
                    response = handleListTools(id);
                    break;
                case "tools/call":
                    response = handleCallTool(request);
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
    
    private ResponseEntity<ObjectNode> handleListTools(String id) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        
        ObjectNode result = response.putObject("result");
        result.set("tools", toolRegistry.getToolsAsJson());
        
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<ObjectNode> handleCallTool(JsonNode request) throws Exception {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", request.get("id").asText());
        
        String toolName = request.get("params").get("name").asText();
        JsonNode argumentsNode = request.get("params").get("arguments");
        
        Map<String, Object> arguments = new HashMap<>();
        if (argumentsNode != null && !argumentsNode.isNull()) {
            arguments = objectMapper.convertValue(argumentsNode, Map.class);
        }
        
        Object result = toolRegistry.callTool(toolName, arguments);
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
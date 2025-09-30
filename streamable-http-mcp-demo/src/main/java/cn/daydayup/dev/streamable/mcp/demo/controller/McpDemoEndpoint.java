package cn.daydayup.dev.streamable.mcp.demo.controller;

import cn.daydayup.dev.streamable.mcp.demo.service.McpDemoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName McpEndpoint
 * @Description MCP接口实现
 * @Author ZhaoYanNing
 * @Date 2025/9/29 19:21
 * @Version 1.0
 */
@RestController
@RequestMapping("/mcp/demo1")
public class McpDemoEndpoint {

    @Resource
    private McpDemoService server;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public void handleGet() {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "GET方法不支持");
    }

    @PostMapping
    public ResponseEntity<ObjectNode> handlePost(@RequestBody String body) throws Exception {
        ObjectNode request = objectMapper.readValue(body, ObjectNode.class);
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
        result.put("protocolVersion", "2024-11-05");

        ObjectNode capabilities = result.putObject("capabilities");

        ObjectNode serverInfo = result.putObject("serverInfo");
        serverInfo.put("name", "三国演义资料库-新版MCP协议");
        serverInfo.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ObjectNode> handleListTools(String id) throws Exception {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);

        ObjectNode result = response.putObject("result");

        List<Map<String, Object>> tools = server.getTools();
        result.set("tools", objectMapper.valueToTree(tools));

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ObjectNode> handleCallTool(ObjectNode request) throws Exception {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", request.get("id").asText());

        String toolName = request.get("params").get("name").asText();
        Map<String, Object> arguments = objectMapper.convertValue(request.get("params").get("arguments"), Map.class);
        Map<String, Object> result = server.callTool(toolName, arguments);

        response.set("result", objectMapper.valueToTree(result));

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ObjectNode> handlePing(String id) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        response.put("result",objectMapper.createObjectNode());

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ObjectNode> handleUnsupportedMethod(String id, String method) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);

        ObjectNode error = response.putObject("error");
        error.put("code", -32601);
        error.put("message", "本服务器不支持这个方法");

        return ResponseEntity.badRequest().body(response);
    }

}

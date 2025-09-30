package cn.daydayup.dev.streamable.mcp.demo.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 *@ClassName McpServerDemo
 *@Description  MCP接口服务实现
 *@Author ZhaoYanNing
 *@Date 2025/9/29 19:22
 *@Version 1.0
 */
@Service
public class McpDemoService {

    private final List<Map<String, Object>> tools = new ArrayList<>();
    private final Map<String, ToolHandler> toolHandlers = new HashMap<>();

    @PostConstruct
    public void initialize() {
        registerWeatherTool();
        registerCalculatorTool();
    }

    public List<Map<String, Object>> getTools() {
        return tools;
    }

    public Map<String, Object> callTool(String toolName, Map<String, Object> arguments) {
        ToolHandler handler = toolHandlers.get(toolName);
        if (handler == null) {
            List<Map<String, Object>> content = new ArrayList<>();
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Tool not found: " + toolName);
            content.add(textContent);

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("isError", true);
            return result;
        }
        return handler.handle(arguments);
    }

    private void registerWeatherTool() {
        Map<String, Object> tool = new HashMap<>();
        tool.put("name", "getWeather");
        tool.put("description", "获取城市天气信息");
        tool.put("inputSchema", createWeatherSchema());

        tools.add(tool);
        toolHandlers.put((String) tool.get("name"), arguments -> {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> response = new HashMap<>();
            try {
                String city = (String) arguments.get("city");
                if (city == null || city.isEmpty()) {
                    Map<String, Object> textContent = new HashMap<>();
                    textContent.put("type", "text");
                    textContent.put("text", "错误：城市名称不能为空");
                    result.add(textContent);
                    response.put("content", result);
                    response.put("isError", true);
                    return response;
                }

                // 模拟天气查询
                String weather = String.format("%s: 晴天，温度25℃", city);
                Map<String, Object> textContent = new HashMap<>();
                textContent.put("type", "text");
                textContent.put("text", weather);
                result.add(textContent);
                response.put("content", result);
                response.put("isError", false);
                return response;
            } catch (Exception e) {
                Map<String, Object> textContent = new HashMap<>();
                textContent.put("type", "text");
                textContent.put("text", "查询错误: " + e.getMessage());
                result.add(textContent);
                response.put("content", result);
                response.put("isError", true);
                return response;
            }
        });
    }

    private void registerCalculatorTool() {
        Map<String, Object> tool = new HashMap<>();
        tool.put("name", "calculate");
        tool.put("description", "简单计算器");
        tool.put("inputSchema", createCalculatorSchema());

        tools.add(tool);
        toolHandlers.put((String) tool.get("name"), arguments -> {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> response = new HashMap<>();
            try {
                Double num1 = Double.parseDouble(arguments.get("num1").toString());
                Double num2 = Double.parseDouble(arguments.get("num2").toString());
                String operation = (String) arguments.get("operation");

                double calculationResult;
                switch (operation) {
                    case "add": calculationResult = num1 + num2; break;
                    case "subtract": calculationResult = num1 - num2; break;
                    case "multiply": calculationResult = num1 * num2; break;
                    case "divide": calculationResult = num1 / num2; break;
                    default: throw new IllegalArgumentException("不支持的运算：" + operation);
                }

                Map<String, Object> textContent = new HashMap<>();
                textContent.put("type", "text");
                textContent.put("text", String.format("计算结果: %.2f", calculationResult));
                result.add(textContent);
                response.put("content", result);
                response.put("isError", false);
                return response;
            } catch (Exception e) {
                Map<String, Object> textContent = new HashMap<>();
                textContent.put("type", "text");
                textContent.put("text", "计算错误: " + e.getMessage());
                result.add(textContent);
                response.put("content", result);
                response.put("isError", true);
                return response;
            }
        });
    }

    private Map<String, Object> createWeatherSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> cityProperty = new HashMap<>();
        cityProperty.put("type", "string");
        cityProperty.put("description", "城市名称");
        properties.put("city", cityProperty);

        schema.put("properties", properties);
        schema.put("required", Arrays.asList("city"));

        return schema;
    }

    private Map<String, Object> createCalculatorSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new HashMap<>();

        Map<String, Object> num1Property = new HashMap<>();
        num1Property.put("type", "number");
        num1Property.put("description", "第一个数字");
        properties.put("num1", num1Property);

        Map<String, Object> num2Property = new HashMap<>();
        num2Property.put("type", "number");
        num2Property.put("description", "第二个数字");
        properties.put("num2", num2Property);

        Map<String, Object> operationProperty = new HashMap<>();
        operationProperty.put("type", "string");
        operationProperty.put("description", "运算类型");
        operationProperty.put("enum", Arrays.asList("add", "subtract", "multiply", "divide"));
        properties.put("operation", operationProperty);

        schema.put("properties", properties);
        schema.put("required", Arrays.asList("num1", "num2", "operation"));

        return schema;
    }

    @FunctionalInterface
    private interface ToolHandler {
        Map<String, Object> handle(Map<String, Object> arguments);
    }
}

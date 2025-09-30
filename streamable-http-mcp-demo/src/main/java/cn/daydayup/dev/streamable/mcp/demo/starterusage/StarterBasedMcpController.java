package cn.daydayup.dev.streamable.mcp.demo.starterusage;

import cn.daydayup.dev.streamable.mcp.starter.annotation.McpServer;
import cn.daydayup.dev.streamable.mcp.starter.core.McpController;
import cn.daydayup.dev.streamable.mcp.starter.core.McpParam;
import cn.daydayup.dev.streamable.mcp.starter.core.McpTool;
import cn.daydayup.dev.streamable.mcp.starter.response.ResponseSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 使用starter的MCP服务示例
 */
@McpServer(
    serverName = "MCP测试服务",
    serverVersion = "1.0.0",
    protocolVersion = "2024-11-05"
)
@RestController
@RequestMapping("/mcp/demo2")
public class StarterBasedMcpController extends McpController {

    @McpTool(name = "getWeather", description = "获取城市天气信息")
    public ResponseSchema getWeather(@McpParam(name = "city", description = "城市名称", required = true) String city) {
        return ResponseSchema.text(String.format("%s: 晴天，温度25℃", city),false);
    }

    @McpTool(name = "calculate", description = "简单计算器")
    public ResponseSchema calculate(
            @McpParam(name = "num1", description = "第一个数字", required = true) Double num1,
            @McpParam(name = "num2", description = "第二个数字", required = true) Double num2,
            @McpParam(name = "operation", description = "运算类型: add, subtract, multiply, divide", required = true) String operation) {
        double calculationResult;
        switch (operation) {
            case "add": calculationResult = num1 + num2; break;
            case "subtract": calculationResult = num1 - num2; break;
            case "multiply": calculationResult = num1 * num2; break;
            case "divide": calculationResult = num1 / num2; break;
            default: throw new IllegalArgumentException("不支持的运算：" + operation);
        }
        return ResponseSchema.text("计算结果：" + calculationResult,false);
    }
}
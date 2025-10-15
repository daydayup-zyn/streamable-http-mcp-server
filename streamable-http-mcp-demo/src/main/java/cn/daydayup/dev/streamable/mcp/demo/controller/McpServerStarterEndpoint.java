package cn.daydayup.dev.streamable.mcp.demo.controller;

import cn.daydayup.dev.streamable.mcp.starter.annotation.McpFunction;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpParam;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpServerEndpoint;
import cn.daydayup.dev.streamable.mcp.starter.core.McpWebUtils;
import cn.daydayup.dev.streamable.mcp.starter.response.ResponseSchema;
import javax.servlet.http.HttpServletRequest;

/**
 * 使用starter的MCP服务示例
 */
@McpServerEndpoint(path = "/mcp/demo2",name = "starter-based-mcp-demo", version = "1.0.0")
public class McpServerStarterEndpoint {

    @McpFunction(name = "getWeather", description = "获取天气信息")
    public ResponseSchema getWeather(@McpParam(name = "city", description = "城市名称", required = true) String city) {
        HttpServletRequest request = McpWebUtils.getCurrentRequest();
        if (request != null) {
            String cookie = request.getHeader("Cookie");
            System.out.println(cookie);
        }
        return ResponseSchema.text(String.format("%s: 晴天，温度25℃", city),false);
    }

    @McpFunction(name = "calculate", description = "计算两个数字")
    public ResponseSchema calculate(
            @McpParam(name = "num1", description = "数字1",required = true) Double num1,
            @McpParam(name = "num2", description = "数字2",required = true) Double num2,
            @McpParam(name = "operation", description = "运算符", enums = {"add", "subtract", "multiply", "divide"}, required = true) String operation) {
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
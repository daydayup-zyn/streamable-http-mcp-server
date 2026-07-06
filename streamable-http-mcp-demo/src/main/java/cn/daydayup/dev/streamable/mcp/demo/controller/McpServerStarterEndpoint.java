package cn.daydayup.dev.streamable.mcp.demo.controller;

import cn.daydayup.dev.streamable.mcp.demo.tavily.TavilyClient;
import cn.daydayup.dev.streamable.mcp.demo.tavily.model.SearchResponse;
import cn.daydayup.dev.streamable.mcp.demo.tavily.model.SearchResult;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpFunction;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpParam;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpServerEndpoint;
import cn.daydayup.dev.streamable.mcp.starter.core.McpWebUtils;
import cn.daydayup.dev.streamable.mcp.starter.response.ResponseSchema;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 使用starter的MCP服务示例
 */
@McpServerEndpoint(path = "/mcp/demo2",name = "starter-based-mcp-demo", version = "1.0.0")
public class McpServerStarterEndpoint {

    @Value("${web.search.key}")
    private String apiKey;

    @McpFunction(name = "getWeather", description = "获取天气信息")
    public ResponseSchema getWeather(@McpParam(name = "city", description = "城市名称", required = true) String city) {
        HttpServletRequest request = McpWebUtils.getCurrentRequest();
        if (request != null) {
            String cookie = request.getHeader("Cookie");
            System.out.println(cookie);
        }
        return ResponseSchema.text(String.format("%s: 晴天，温度25℃", city),false);
    }

    @McpFunction(name = "webSearch", description = "网络搜索")
    public ResponseSchema webSearch(
            @McpParam(name = "query", description = "查询内容", required = true) String query) {
        System.out.println("调用了webSearch函数，查询内容：" + query);
        TavilyClient tavilyClient = new TavilyClient(apiKey);
        String finalResult = "";
        try {
            SearchResponse response = tavilyClient.search(query, "advanced");

            if (response.getResults() != null) {
                for (SearchResult result : response.getResults()) {
                    finalResult += "\n  Title: " + result.getTitle() + "\n  URL: " + result.getUrl() + "\n  Content: " + result.getContent() + "\n  Score: " + result.getScore();
                }
            } else {
                finalResult = "No results found.";
            }

        } catch (IOException e) {
            finalResult = "An error occurred during the search: " + e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                tavilyClient.close();
            } catch (IOException e) {
                System.err.println("Failed to close Tavily client: " + e.getMessage());
            }
        }
        return ResponseSchema.text("搜索结果：" + finalResult,false);
    }

    @McpFunction(name = "test", description = "网络搜索")
    public ResponseSchema test() {
        return ResponseSchema.text("搜索结果：就将计就计",false);
    }
}
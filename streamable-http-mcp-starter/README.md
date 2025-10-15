# Streamable HTTP MCP Starter

该starter提供了一种简便的方式来在Spring Boot应用程序中集成MCP（Model Controller Protocol）服务。

## 使用方法

### 基本使用方式

1. 在你的Spring Boot项目中添加依赖：

```xml
<dependency>
    <groupId>cn.daydayup.dev</groupId>
    <artifactId>streamable-http-mcp-starter</artifactId>
    <version>${version}</version>
</dependency>
```

2. 创建一个控制器类并使用[@McpServerEndpoint](file:///D:/IdeaProjects/streamable-http-mcp-server/streamable-http-mcp-starter/src/main/java/cn/daydayup/dev/streamable/mcp/starter/annotation/McpServerEndpoint.java#L16-L34)注解：

```java
@McpServerEndpoint(
    path = "/mcp",
    name = "我的MCP服务",
    version = "1.0.0"
)
public class MyMcpController {
    
    @McpFunction(name = "getWeather", description = "获取天气信息")
    public ResponseSchema getWeather(@McpParam(name = "city", description = "城市名称", required = true) String city) {
        return ResponseSchema.text(city + ": 晴天，25℃", false);
    }
}
```

3. 启动你的Spring Boot应用，MCP服务将在指定路径下可用。

## 访问HTTP请求上下文

在某些场景下，您可能需要在MCP工具方法中访问HTTP请求信息（如请求头、客户端IP等）。可以通过以下方式实现：

```java
@McpServerEndpoint(
    path = "/mcp",
    name = "我的MCP服务",
    version = "1.0.0"
)
public class MyMcpController {
    
    @McpFunction(name = "getUserInfo", description = "获取用户信息")
    public ResponseSchema getUserInfo() {
        // 获取当前HTTP请求
        HttpServletRequest request = McpWebUtils.getCurrentRequest();
        
        // 获取请求头信息
        String userAgent = McpWebUtils.getRequestHeader("User-Agent");
        
        // 获取客户端IP
        String clientIp = McpWebUtils.getClientIpAddress();
        
        // 使用获取到的信息
        String userInfo = String.format("User-Agent: %s, Client IP: %s", userAgent, clientIp);
        return ResponseSchema.text(userInfo, false);
    }
}
```

## 特性

- 兼容Spring Boot 2.1.x版本
- 支持通过[@McpFunction](file:///D:/IdeaProjects/streamable-http-mcp-server/streamable-http-mcp-starter/src/main/java/cn/daydayup/dev/streamable/mcp/starter/annotation/McpFunction.java#L14-L26)注解定义MCP工具
- 支持通过[@McpParam](file:///D:/IdeaProjects/streamable-http-mcp-server/streamable-http-mcp-starter/src/main/java/cn/daydayup/dev/streamable/mcp/starter/core/McpParam.java#L14-L30)注解定义工具参数
- 自动注册和管理MCP工具
- 灵活的配置选项
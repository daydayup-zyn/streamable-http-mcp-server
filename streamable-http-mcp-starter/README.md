# Streamable HTTP MCP Starter

该starter提供了一种简便的方式来在Spring Boot应用程序中集成MCP（Model Controller Protocol）服务。

## 使用方法

有两种使用方式：

### 方式一：使用@EnableMcpServer注解（向后兼容）

1. 在你的Spring Boot项目中添加依赖：

```xml
<dependency>
    <groupId>cn.daydayup.dev</groupId>
    <artifactId>streamable-http-mcp-starter</artifactId>
    <version>${version}</version>
</dependency>
```

2. 创建一个控制器类并使用`@EnableMcpServer`注解：

```java
@EnableMcpServer(
    path = "/mcp",
    serverName = "我的MCP服务",
    serverVersion = "1.0.0"
)
public class MyMcpController {
    
    @McpTool(name = "getWeather", description = "获取天气信息")
    public Map<String, Object> getWeather(@McpParam(name = "city", description = "城市名称", required = true) String city) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", city + ": 晴天，25℃");
        content.add(textContent);
        result.put("content", content);
        result.put("isError", false);
        return result;
    }
}
```

### 方式二：继承McpController基类（推荐）

1. 在你的Spring Boot项目中添加依赖：

```xml
<dependency>
    <groupId>cn.daydayup.dev</groupId>
    <artifactId>streamable-http-mcp-starter</artifactId>
    <version>${version}</version>
</dependency>
```

2. 创建一个控制器类继承McpController并使用`@McpServer`注解：

```java
@McpServer(
    serverName = "我的MCP服务",
    serverVersion = "1.0.0"
)
@RestController
@RequestMapping("/mcp")
public class MyMcpController extends McpController {
    
    @McpTool(name = "getWeather", description = "获取天气信息")
    public ResponseSchema getWeather(@McpParam(name = "city", description = "城市名称", required = true) String city) {
        return ResponseSchema.text(city + ": 晴天，25℃", false);
    }
}
```

3. 启动你的Spring Boot应用，MCP服务将在指定路径下可用。

## 特性

- 兼容Spring Boot 2.1.x到3.x版本
- 支持通过注解方式定义MCP工具
- 支持通过[@McpParam](file:///D:/IdeaProjects/streamable-http-mcp-server/streamable-http-mcp-starter/src/main/java/cn/daydayup/dev/streamable/mcp/starter/core/McpParam.java#L14-L30)注解定义工具参数
- 自动注册和管理MCP工具
- 灵活的配置选项
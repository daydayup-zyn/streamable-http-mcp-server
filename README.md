# Streamable HTTP MCP Server

这是一个基于HTTP协议的MCP（Model Controller Protocol）服务器实现，支持通过注解方式快速集成到Spring Boot项目中。

## 项目结构

- `streamable-http-mcp-starter`: 提供可重用的starter模块，方便集成到其他Spring Boot项目
- `streamable-http-mcp-demo`: 演示如何使用starter模块创建MCP服务

## 功能特性

- 兼容Spring Boot 2.1.x版本
- 支持通过注解方式快速创建MCP服务
- 提供工具注册和调用机制
- 支持JSON-RPC 2.0协议

## 使用方法

### 作为依赖引入

在你的Spring Boot项目中添加以下依赖：

```xml
<dependency>
    <groupId>cn.daydayup.dev</groupId>
    <artifactId>streamable-http-mcp-starter</artifactId>
    <version>1.0</version>
</dependency>
```

### 创建MCP服务

创建一个控制器类并使用`@EnableMcpServer`注解：

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

## 支持的MCP方法

- `initialize`: 初始化连接
- `tools/list`: 列出所有可用工具
- `tools/call`: 调用指定工具
- `ping`: 心跳检测

## 构建和运行

```bash
# 构建项目
mvn clean install

# 运行demo
mvn spring-boot:run -pl streamable-http-mcp-demo
```
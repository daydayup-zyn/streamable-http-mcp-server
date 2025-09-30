# Streamable HTTP MCP Server

这是一个基于HTTP协议的MCP（Model Controller Protocol）服务器实现，支持通过注解方式快速集成到Spring Boot项目中。

## 项目结构

- `streamable-http-mcp-starter`: 提供可重用的starter模块，方便集成到其他Spring Boot项目
- `streamable-http-mcp-demo`: 演示如何使用starter模块创建MCP服务

## 功能特性

- 兼容Spring Boot 2.1.x到3.x版本
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
@EnableMcpServer("/mcp")
@RestController
public class MyMcpController extends BaseMcpController {
    
    @McpTool(name = "echo", description = "回显消息")
    public Map<String, Object> echo(String message) {
        // 实现工具逻辑
    }
    
    @Override
    protected void registerTools() {
        // 可选：手动注册工具
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
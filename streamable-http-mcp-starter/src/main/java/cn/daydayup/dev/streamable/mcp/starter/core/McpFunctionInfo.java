package cn.daydayup.dev.streamable.mcp.starter.core;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName McpFunctionInfo
 * @Description MCP方法信息
 * @Author ZhaoYanNing
 * @Date 2025/9/30 15:11
 * @Version 1.0
 */
@Getter
public class McpFunctionInfo {

    private final String          name;
    private final String          description;
    private final Method          method;
    private final List<ParamInfo> params;

    public McpFunctionInfo(String name, String description, Method method, List<ParamInfo> params) {
        this.name = name;
        this.description = description;
        this.method = method;
        this.params = params;
    }

    @Getter
    public static class ParamInfo {

        private final String   name;
        private final String   description;
        private final String[] enums;
        private final boolean  required;
        private final Class<?> type;

        public ParamInfo(String name, String description, String[] enums, boolean required, Class<?> type) {
            this.name = name;
            this.description = description;
            this.enums = enums;
            this.required = required;
            this.type = type;
        }
    }
}

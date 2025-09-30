package cn.daydayup.dev.streamable.mcp.starter.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ResponseSchema
 * @Description 响应模式
 * @Author ZhaoYanNing
 * @Date 2025/9/30 13:58
 * @Version 1.0
 */
public class ResponseSchema extends HashMap<String, Object> {

    public static ResponseSchema text(String text, boolean isError){
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", text);
        result.add(textContent);

        ResponseSchema responseSchema = new ResponseSchema();
        responseSchema.put("content", result);
        responseSchema.put("isError", isError);
        return responseSchema;
    }
}

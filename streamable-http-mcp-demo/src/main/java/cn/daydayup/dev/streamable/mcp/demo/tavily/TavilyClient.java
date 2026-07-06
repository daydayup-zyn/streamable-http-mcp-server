package cn.daydayup.dev.streamable.mcp.demo.tavily; // Updated package

import cn.daydayup.dev.streamable.mcp.demo.tavily.model.SearchRequest;
import cn.daydayup.dev.streamable.mcp.demo.tavily.model.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TavilyClient {

    private static final String TAVILY_API_URL = "https://api.tavily.com/search";
    private final String apiKey;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TavilyClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    public SearchResponse search(String query) throws IOException {
        SearchRequest searchRequest = new SearchRequest(query);
        return performSearch(searchRequest);
    }

    public SearchResponse search(String query, String searchDepth) throws IOException {
        SearchRequest searchRequest = new SearchRequest(query, searchDepth);
        return performSearch(searchRequest);
    }

    private SearchResponse performSearch(SearchRequest searchRequest) throws IOException {
        HttpPost httpPost = new HttpPost(TAVILY_API_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + this.apiKey);

        String jsonRequest = objectMapper.writeValueAsString(searchRequest);
        httpPost.setEntity(new StringEntity(jsonRequest, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("API request failed with status code: " +
                        response.getStatusLine().getStatusCode() +
                        " and response: " + jsonResponse);
            }
            return objectMapper.readValue(jsonResponse, SearchResponse.class);
        }
    }

    public void close() throws IOException {
        if (this.httpClient != null) {
            this.httpClient.close();
        }
    }
}

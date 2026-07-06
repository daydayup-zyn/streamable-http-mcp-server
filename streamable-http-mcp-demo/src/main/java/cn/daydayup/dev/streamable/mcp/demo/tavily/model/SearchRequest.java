package cn.daydayup.dev.streamable.mcp.demo.tavily.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRequest {

    @JsonProperty("query")
    private String query;

    // Optional parameters can be added here later, e.g.:
    @JsonProperty("search_depth")
    private String searchDepth;

    // @JsonProperty("include_answer")
    // private Boolean includeAnswer;
    //
    // @JsonProperty("include_images")
    // private Boolean includeImages;
    //
    // @JsonProperty("include_raw_content")
    // private Boolean includeRawContent;
    //
    // @JsonProperty("max_results")
    // private Integer maxResults;
    //
    // @JsonProperty("api_key") // If not using Authorization header
    // private String apiKey;

    public SearchRequest(String query) {
        this.query = query;
    }

    public SearchRequest(String query, String searchDepth) {
        this.query = query;
        this.searchDepth = searchDepth;
    }

    public String getQuery() {
        return query;
    }

    public String getSearchDepth() {
        return searchDepth;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setSearchDepth(String searchDepth) {
        this.searchDepth = searchDepth;
    }

    // Getters and setters for optional parameters would go here
}

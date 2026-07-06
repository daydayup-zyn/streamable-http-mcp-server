package cn.daydayup.dev.streamable.mcp.demo.tavily.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

    @JsonProperty("query")
    private String query;

    @JsonProperty("follow_up_questions")
    private List<String> followUpQuestions; // Assuming it's a list of strings

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("images")
    private List<String> images; // Assuming it's a list of image URLs (strings)

    @JsonProperty("results")
    private List<SearchResult> results;

    @JsonProperty("response_time")
    private double responseTime;

    // Getters and Setters
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getFollowUpQuestions() {
        return followUpQuestions;
    }

    public void setFollowUpQuestions(List<String> followUpQuestions) {
        this.followUpQuestions = followUpQuestions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "query='" + query + '\'' +
                ", followUpQuestions=" + followUpQuestions +
                ", answer='" + answer + '\'' +
                ", images=" + images +
                ", results=" + results +
                ", responseTime=" + responseTime +
                '}';
    }
}

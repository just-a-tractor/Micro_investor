package ru.helpers.micro_investor;

import feign.Param;
import feign.RequestLine;
import org.json.simple.JSONObject;

public interface GiphyRequest {
    @RequestLine("GET /v1/gifs/search?api_key={apiKey}&q={condition}&limit=1&offset=0&rating=r&lang=en")
    JSONObject getResponse(@Param("apiKey") String apiKey, @Param("condition") String condition);
}

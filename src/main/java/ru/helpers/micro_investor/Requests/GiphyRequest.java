package ru.helpers.micro_investor.Requests;

import feign.Param;
import feign.RequestLine;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

public interface GiphyRequest {
    @RequestLine("GET /v1/gifs/random?api_key={apiKey}&tag={condition}&rating=r")
    JSONObject getResponse(@Param("apiKey") String apiKey, @Param("condition") String condition);
}

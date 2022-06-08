package ru.helpers.micro_investor.Requests;

import feign.Param;
import feign.RequestLine;
import org.json.simple.JSONObject;

public interface CurrencyCourseRequest {
    @RequestLine("GET /api/{query}.json?app_id={appId}&base={base}")
    JSONObject getCourse(@Param String query, @Param String appId, @Param String base);
}

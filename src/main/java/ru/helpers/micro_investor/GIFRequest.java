package ru.helpers.micro_investor;

import com.google.gson.Gson;
import feign.Param;
import feign.RequestLine;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public interface GIFRequest {
    @RequestLine("GET /media/{media}/giphy.gif")
    String getGif(@Param String media);
}

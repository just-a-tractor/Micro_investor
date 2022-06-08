package ru.helpers.micro_investor.Requests;

import feign.Param;
import feign.RequestLine;
import feign.Response;

public interface GIFRequest { // getMedia
    @RequestLine("GET /media/{media}/giphy.gif")
    Response getGif(@Param String media);
}

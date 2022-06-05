package ru.helpers.micro_investor.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;
import feign.Feign;
import feign.codec.StringDecoder;
import feign.gson.GsonDecoder;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.helpers.micro_investor.GIFRequest;
import ru.helpers.micro_investor.GiphyRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@RestController
public class CurrencyController {

    @Value("${Giphy.APIkey}")
    private String apiKey;

    public CurrencyController() {
    }
    @ResponseBody
    @GetMapping(value = "/gif", produces = MediaType.IMAGE_GIF_VALUE)
    public byte[] getGIF() throws IOException {

        GiphyRequest giphyRequest = Feign.builder()
                .decoder(new GsonDecoder())
                .target(GiphyRequest.class, "https://api.giphy.com");

        JSONObject base_url = giphyRequest.getResponse(apiKey, "rich");
        ArrayList data = (ArrayList) base_url.get("data");
        LinkedTreeMap image0 = (LinkedTreeMap) data.get(0);
        LinkedTreeMap images = (LinkedTreeMap) image0.get("images");
        LinkedTreeMap original = (LinkedTreeMap) images.get("original");
        String image_url = (String) original.get("url");
        String[] octet = image_url.split("/");
        String media = octet[4];

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        GIFRequest gifRequest = Feign.builder()
                .decoder(new StringDecoder())
                .target(GIFRequest.class, "https://"+octet[2]);

        return gifRequest.getGif(media).getBytes(StandardCharsets.UTF_8);
    }
}

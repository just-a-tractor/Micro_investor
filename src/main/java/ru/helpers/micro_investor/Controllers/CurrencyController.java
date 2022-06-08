package ru.helpers.micro_investor.Controllers;

import com.google.gson.internal.LinkedTreeMap;
import feign.Feign;
import feign.Response;
import feign.gson.GsonDecoder;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.helpers.micro_investor.Requests.CurrencyCourseRequest;
import ru.helpers.micro_investor.Requests.GIFRequest;
import ru.helpers.micro_investor.Requests.GiphyRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@RestController
public class CurrencyController {

    @Value("${Giphy.APIKey}")
    private String giphyApiKey;
    @Value("${Currency.APIKey}")
    private String currencyApiKEY;
    @Value("${ComparedCurrency}")
    private String comparedCurrency;
    @Value("${GifApiUrl}")
    private String gifApiUrl;
    @Value("${CurrencyApiUrl}")
    private String currencyApiUrl;
    @Value("${ReactionTag.higher}")
    private String higher;
    @Value("${ReactionTag.lower}")
    private String lower;
    @Value("${ReactionTag.same}")
    private String same;

    private Set validCurrenciesSet;

    @ResponseBody
    @GetMapping(value = "/rate/{currencyArg}", produces = MediaType.IMAGE_GIF_VALUE)
    public ResponseEntity getGIF(@PathVariable String currencyArg) throws IOException {

        if (!currencyCodeValidation(currencyArg))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        CurrencyCourseRequest currencyCourseRequest = Feign.builder().decoder(new GsonDecoder())
        .target(CurrencyCourseRequest.class, currencyApiUrl);

        double[] rates = getRates(currencyCourseRequest, currencyArg);
        String queryTag = rates[0] < rates[1] ? higher: rates[0] == rates[1] ? same: lower;

        JSONObject giphyResponse = getGiphyResponse(queryTag);
        String image_url = getGifUrl(giphyResponse);

        Response gifResponse = getGifResponse(image_url);
        return getByteArray(gifResponse);
    }

    boolean currencyCodeValidation(String currencyCode){
        if (validCurrenciesSet == null)
            validCurrenciesSet = ((LinkedTreeMap) Feign.builder().decoder(new GsonDecoder())
            .target(CurrencyCourseRequest.class, currencyApiUrl)
            .getCourse("latest", currencyApiKEY, comparedCurrency).get("rates")).keySet();
        return validCurrenciesSet.contains(currencyCode);
    }

    /**
     * Function makes a request to openexchangerates api and returns today's
     * and yesterday's courses(in relation to default) of given currency
     * @param currencyCourseRequest CurrencyCourseRequest interface realization, method getCourse returns JSON
     * @param currency Currency, compared with default
     * @return Double array of two exchange rates
     */
    double[] getRates(CurrencyCourseRequest currencyCourseRequest, String currency){
        String yesterdayDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        .format(LocalDateTime.now().minus(1, ChronoUnit.DAYS));

        JSONObject yesterday = currencyCourseRequest.getCourse("historical/" + yesterdayDate, currencyApiKEY, comparedCurrency);
        Double yesterdayRate = (Double) ((LinkedTreeMap) yesterday.get("rates")).get(currency);
        JSONObject today = currencyCourseRequest.getCourse("latest", currencyApiKEY, comparedCurrency);
        Double todayRate = (Double) ((LinkedTreeMap) today.get("rates")).get(currency);

        return new double[]{yesterdayRate, todayRate};
    }

    /**
     * @param queryTag tag of image searched at giphy api
     * @return Service response JSON
     */
    JSONObject getGiphyResponse(String queryTag){
        GiphyRequest giphyRequest = Feign.builder().decoder(new GsonDecoder()).target(GiphyRequest.class, gifApiUrl);
        return giphyRequest.getResponse(giphyApiKey, queryTag);
    }

    /**
     * Parses giphy JSON to get url of the needed image
     * @param jsonObject Giphy response
     * @return Image url
     */
    String getGifUrl(JSONObject jsonObject){
        LinkedTreeMap data = (LinkedTreeMap) jsonObject.get("data");
        LinkedTreeMap images = (LinkedTreeMap) data.get("images");
        LinkedTreeMap original = (LinkedTreeMap) images.get("original");
        return (String) original.get("url");
    }

    /**
     * Uses GIFRequest Feign interface to get image from url
     * @param imageUrl
     * @return GIFRequest response
     */
    Response getGifResponse(String imageUrl){
        String[] octet = imageUrl.split("/");
        String media = octet[4];
        GIFRequest gifRequest = Feign.builder().target(GIFRequest.class, "https://"+octet[2]);
        return gifRequest.getGif(media);
    }

    /**
     * Uses Stream to parse GIFRequest response to get byte array of the image
     * @param resp GIFRequest response
     * @return ResponseEntity with needed byte array in body
     * @throws IOException
     */
    ResponseEntity getByteArray(Response resp) throws IOException {
        byte[] ans = IOUtils.toByteArray(resp.body().asInputStream());
        return new ResponseEntity<>(ans, HttpStatus.OK);
    }

}

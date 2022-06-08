package ru.helpers.micro_investor.Controllers;

import com.google.gson.internal.LinkedTreeMap;
import feign.Feign;
import feign.Response;
import feign.gson.GsonDecoder;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.helpers.micro_investor.Requests.CurrencyCourseRequest;
import ru.helpers.micro_investor.Requests.GiphyRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CurrencyControllerTest {

    @Autowired
    CurrencyController currencyController;

    @MockBean
    private CurrencyCourseRequest currencyCourseRequest;

    @Test
    void getGIF() throws IOException {
        ResponseEntity ans = currencyController.getGIF("RUB");
        assert ans.getStatusCode() == HttpStatus.OK && ans.hasBody();
        assert currencyController.getGIF("RAQ").getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    void currencyCodeValidation() {
        assertTrue(currencyController.currencyCodeValidation("RUB"));
        assertTrue(currencyController.currencyCodeValidation("EUR"));
        assertFalse(currencyController.currencyCodeValidation("eur"));
        assertFalse(currencyController.currencyCodeValidation("RAQ"));
        assertFalse(currencyController.currencyCodeValidation("-1"));
        assertFalse(currencyController.currencyCodeValidation(""));
    }

    @Test
    void getRates(){
        String yesterdayDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .format(LocalDateTime.now().minus(1, ChronoUnit.DAYS));

        LinkedTreeMap<String, LinkedTreeMap<String, Double>> json = new LinkedTreeMap<>();
        LinkedTreeMap<String, Double> rates = new LinkedTreeMap<>();
                rates.put("USD", 1.0);
                rates.put("RUB", 170.9);
        json.put("rates", rates);
        JSONObject jsonObject = new JSONObject(json);

        Mockito.when(currencyCourseRequest.getCourse
        ("latest", "50df5d60645448ff9ca4ca3900879b11", "USD")).thenReturn(jsonObject);

        Mockito.when(currencyCourseRequest.getCourse
        ("historical/" + yesterdayDate, "50df5d60645448ff9ca4ca3900879b11", "USD")).thenReturn(jsonObject);

        double[] ans = currencyController.getRates(currencyCourseRequest, "RUB");
        assertNotNull(ans);
    }

    @Test
    void getGiphyResponse() {
        assertNotNull(currencyController.getGiphyResponse("rich").get("data"));
    }

    @Test
    void getGifUrl() {
        GiphyRequest giphyRequest = Feign.builder().decoder(new GsonDecoder()).target(GiphyRequest.class, "https://api.giphy.com");
        String ans = currencyController.getGifUrl(giphyRequest.getResponse("r706tx79pycK5OFD2tWy4zOyFlqhIoIs", "rich"));
        assertTrue(ans.contains("giphy.gif"));
    }

    @Test
    void getGifResponse() {
        assertNotNull(currencyController.getGifResponse("https://media1.giphy.com/media/H0uLRCd8JIhRS/giphy.gif"));
    }

    @Test
    void getByteArray() throws IOException {
        Response a = currencyController.getGifResponse("https://media1.giphy.com/media/H0uLRCd8JIhRS/giphy.gif");
        ResponseEntity ans = currencyController.getByteArray(a);
        assert ans.getStatusCode() == HttpStatus.OK && ans.hasBody();
    }
}

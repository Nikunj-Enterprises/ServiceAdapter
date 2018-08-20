package com.easyapper.serviceadapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
public class CurrencyAdapter {
    private final String currencyConvertUrl = "https://data.fixer.io/api/convert?access_key=09c478363427c8121495720a274255b8";
    private final String currencyHistoryUrlTemplate = "https://data.fixer.io/api/%s?access_key=09c478363427c8121495720a274255b8";

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/currency/{fromCurrency}/convert/{toCurrency}")
    public String convertCurrency(@PathVariable("fromCurrency") String fromCurrency,
                                  @PathVariable("toCurrency") String toCurrency,
                                  @RequestParam(value = "amount", defaultValue = "1", required = false) Float amount){
        String currencyConversionUrl = currencyConvertUrl +"&from="+fromCurrency+"&to="+toCurrency+"&amount="+amount.toString();
        ResponseEntity<String> response = restTemplate.getForEntity(currencyConversionUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
            if(!root.path("error").isMissingNode()){
                String message = root.path("error").path("info").asText();
                return String.format("{ \"messages\": [ {\"text\": \"%s\"} ]}",message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = root.path("result").asText();

        return "{\"set_attributes\":{\"currencyConversionValue\":\""+result+"\"}}";
    }


    @RequestMapping("/currency/{fromCurrency}/historicalrate/{toCurrency}")
    public String getHistoricalCurrencyRate(@PathVariable("fromCurrency") String fromCurrency,
                                            @PathVariable("toCurrency") String toCurrency,
                                            @RequestParam("date") String date){
        String historicalRateUrl =
                String.format(currencyHistoryUrlTemplate, date)+"&symbols="+toCurrency+"&base="+fromCurrency;

        ResponseEntity<String> response = restTemplate.getForEntity(historicalRateUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
            if(!root.path("error").isMissingNode()){
                String message = root.path("error").path("info").asText();
                return String.format("{ \"messages\": [ {\"text\": \"%s\"} ]}",message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = root.path("rates").path(toCurrency).asText();

        return "{\"set_attributes\":{\"currencyConversionValue\":\""+result+"\"}}";
    }
}

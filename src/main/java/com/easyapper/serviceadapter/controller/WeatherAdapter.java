package com.easyapper.serviceadapter.controller;

import com.easyapper.serviceadapter.util.CityUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;

@RestController
public class WeatherAdapter {

    private final String weatherUrl = "http://api.wunderground.com/api/034ab0a15360a793/";


    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/weather/conditions/{city}")
    public String getWeatherCondition(@PathVariable("city") String city) {
        String weatherConditionUrl = weatherUrl+"conditions/q/GR/"+CityUtil.getCityNameForUrl(city) +".json";

        ResponseEntity<String> response = restTemplate.getForEntity(weatherConditionUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
            if(!root.path("response").path("error").isMissingNode()){
                String message = root.path("response").path("error").path("description").asText();
                return String.format("{ \"messages\": [ {\"text\": \"%s\"} ]}",message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonNode condition = root.path("current_observation");
        String weather = condition.path("weather").asText();
        String feelsLikeTemp = condition.path("feelslike_string").asText();
        String relative_humidity = condition.path("relative_humidity").asText();
        String wind = condition.path("wind_string").asText();
        String wind_dir = condition.path("wind_dir").asText();

        return "{\"set_attributes\":{\"weatherFeelsLikeTempC\":\""+feelsLikeTemp+
                "\", \"weatherDescription\":\""+weather+"\", \"humidity\":\""+relative_humidity+
                "\", \"wind\":\""+wind+"\", \"wind_dir\":\""+wind_dir+"\"}}";
    }

    @RequestMapping("/weather/forecast/{city}")
    public String getWeatherForecast(@PathVariable("city") String city) {
        String weatherConditionUrl = weatherUrl + "forecast/q/GR/" + CityUtil.getCityNameForUrl(city) + ".json";

        ResponseEntity<String> response = restTemplate.getForEntity(weatherConditionUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
            if (!root.path("response").path("error").isMissingNode()) {
                String message = root.path("response").path("error").path("description").asText();
                return String.format("{ \"messages\": [ {\"text\": \"%s\"} ]}", message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonNode forecast = root.path("forecast").path("txt_forecast").path("forecastday");
        Iterator<JsonNode> itr = forecast.elements();
        StringBuilder builder = new StringBuilder();

        while (itr.hasNext()) {
            JsonNode node = itr.next();
            String title = node.path("title").asText();
            String subtitle = node.path("fcttext").asText();
            String image_url = node.path("icon_url").asText();
            builder.append(String.format("{\"title\":\"%s\",\"image_url\":\"%s\",\"subtitle\":\"%s\"},", title, image_url, subtitle));
        }

        int length = builder.length();
        if (builder.lastIndexOf(",") == length - 1) {
            builder.deleteCharAt(length -1);
        }

        String responseStr = "{\n" +
                " \"messages\": [\n" +
                "    {\n" +
                "      \"attachment\":{\n" +
                "        \"type\":\"template\",\n" +
                "        \"payload\":{\n" +
                "          \"template_type\":\"generic\",\n" +
                "          \"image_aspect_ratio\": \"square\",\n" +
                "          \"elements\":[" + builder.toString() + "]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return responseStr;
    }

    @RequestMapping("/weather/hourlyforecast/{city}")
    public String getWeatherHourlyForecast(@PathVariable("city") String city) {
        String weatherConditionUrl = weatherUrl + "hourly/q/GR/" + CityUtil.getCityNameForUrl(city) + ".json";

        ResponseEntity<String> response = restTemplate.getForEntity(weatherConditionUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
            if (!root.path("response").path("error").isMissingNode()) {
                String message = root.path("response").path("error").path("description").asText();
                return String.format("{ \"messages\": [ {\"text\": \"%s\"} ]}", message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonNode forecast = root.path("hourly_forecast");
        Iterator<JsonNode> itr = forecast.elements();
        StringBuilder builder = new StringBuilder();

        while (itr.hasNext()) {
            JsonNode node = itr.next();
            String subtitle = node.path("FCTTIME").path("pretty").asText();
            String title = node.path("wx").asText();
            String image_url = node.path("icon_url").asText();
            builder.append(String.format("{\"title\":\"%s\",\"image_url\":\"%s\",\"subtitle\":\"%s\"},", title, image_url, subtitle));
        }

        int length = builder.length();
        if (builder.lastIndexOf(",") == length - 1) {
            builder.deleteCharAt(length -1);
        }

        String responseStr = "{\n" +
                " \"messages\": [\n" +
                "    {\n" +
                "      \"attachment\":{\n" +
                "        \"type\":\"template\",\n" +
                "        \"payload\":{\n" +
                "          \"template_type\":\"generic\",\n" +
                "          \"image_aspect_ratio\": \"horizontal\",\n" +
                "          \"elements\":[" + builder.toString() + "]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return responseStr;
    }
}

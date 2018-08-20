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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

@RestController
public class FlightAdapter {
    private final String flightUrlTemplate = "http://aviation-edge.com/api/public/%s?key=fab70f-dbd351-b5972d-d418c6-073003";

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/flights/nearbyairports")
    public String findNearByAirports(@RequestParam("lat") String lat,
                                     @RequestParam("lng") String lng){
        String responseTemplates = "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"attachment\": {\n" +
                "        \"type\": \"template\",\n" +
                "        \"payload\": {\n" +
                "          \"template_type\": \"button\",\n" +
                "          \"text\": \"These are the airports I found near you!\",\n" +
                "          \"buttons\": [\n" +
                "            %s" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "   }]\n" +
                "}";

        //http://aviation-edge.com/api/public/nearby?key=fab70f-dbd351-b5972d-d418c6-073003&lat=12.924&lng=77.682
        return null;
    }

    @RequestMapping("/flights/{departureAt}/route/{arrivalAt}")
    public String findFlightsForRoute(@PathVariable("departureAt") String departureAt,
                                      @PathVariable("arrivalAt") String arrivalAt,
                                      @RequestParam("date") String journeyDate){
        //http://aviation-edge.com/api/public/routes?key=fab70f-dbd351-b5972d-d418c6-073003&departureIata=ATH&arrivalIata=FCO
        String routeFlightsUrl =
                String.format(flightUrlTemplate,"routes")+"&departureIata="+departureAt+"&arrivalIata="+arrivalAt;
        ResponseEntity<String> response = restTemplate.getForEntity(routeFlightsUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
            if(!root.path("error").isMissingNode()){
                String message = root.path("error").path("text").asText();
                return String.format("{ \"messages\": [ {\"text\": \"%s\"} ]}",message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<JsonNode> itr = root.elements();
        StringBuilder builder = new StringBuilder();

        while (itr.hasNext()) {
            JsonNode node = itr.next();
            String departingFrom = node.path("departureIata").asText();
            String departureTime = node.path("departureTime").asText();
            String arrivingAt = node.path("arrivalIata").asText();
            String arrivalTime = node.path("arrivalTime").asText();
            String airline = node.path("airlineIata").asText();
            String flightNo = node.path("flightNumber").asText();

            String message =
                    "Flight "+airline+flightNo+": "+departingFrom+"("+departureTime+") ->"+" "+arrivingAt+"("+arrivalTime+")";
            builder.append(String.format("{\"text\":\"%s\"},",message));
        }
        int length = builder.length();
        if (builder.lastIndexOf(",") == length - 1) {
            builder.deleteCharAt(length -1);
        }

        return String.format("{ \"messages\": [ %s ]}",builder.toString());
    }

    @RequestMapping("/flights/status")
    public String findFlightStatus(@RequestParam("flight") String flight){
        //http://aviation-edge.com/api/public/flights?key=fab70f-dbd351-b5972d-d418c6-073003&flight[iataNumber]=W8583
        String routeFlightsUrl =
                String.format(flightUrlTemplate,"flights")+"&flight[iataNumber]="+flight;
        ResponseEntity<String> response = restTemplate.getForEntity(routeFlightsUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
            if(!root.path("error").isMissingNode()){
                String message = root.path("error").path("text").asText();
                return String.format("{ \"messages\": [ {\"text\": \"%s\"} ]}",message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<JsonNode> itr = root.elements();
        StringBuilder builder = new StringBuilder();

        while (itr.hasNext()) {
            JsonNode node = itr.next();
            JsonNode geography = node.path("geography");
            JsonNode speed = node.path("speed");
            String status = node.path("status").asText();
            String latitude = geography.path("latitude").asText();
            String longitude = geography.path("longitude").asText();
            String altitude = geography.path("altitude").asText();
            String direction = geography.path("direction").asText();
            String forwardSpeed = speed.path("horizontal").asText();
            String isGround = speed.path("isGround").asText();
            String verticalSpeed = speed.path("vertical").asText();
            Date date = new Date(node.path("system").path("updated").asLong());
            DateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            formater.setTimeZone(TimeZone.getTimeZone("Europe/Athens"));
            String timeOfObservation = formater.format(date);

            String message =
                    "Flight "+flight+" "+status+" at altitude of "+altitude+" with lat("+latitude+") and lang("+longitude+")";
            builder.append(String.format("{\"text\":\"%s\"},",message));
        }
        int length = builder.length();
        if (builder.lastIndexOf(",") == length - 1) {
            builder.deleteCharAt(length -1);
        }

        return String.format("{ \"messages\": [ %s ]}",builder.toString());

        // try template at http://pastebin.com/raw/tAWsv3Pu
    }
}

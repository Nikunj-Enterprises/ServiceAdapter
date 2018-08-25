package com.easyapper.serviceadapter.controller;

import com.easyapper.serviceadapter.model.AlterraTravelQuery;
import com.easyapper.serviceadapter.util.EpochConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class QueryParserAdapter {
    /**
     *   http://13.233.11.170:8091/queryparser/getDateForUserInput?intent=flight_booking&context=departure&input={{userDepartureTime}}
     *
     *   returns set_attribute departingOn(dd/mm/yyyy)
     *
     *   http://13.233.11.170:8091/queryparser/getDateForUserInput?intent=flight_booking&context=arrival&input={{userReturnDate}}
     *
     *   returns set_attribute returningOn(dd/mm/yyyy), typeFlight=oneway/round
     */

    private final String flightUrlTemplate =
            "https://api.skypicker.com/flights?flyFrom=%s&to=%s&dateFrom=%s&dateTo=%s&partner=picky";

    private final String onewayFlightUrlTemplate =
            "https://api.skypicker.com/flights?flyFrom=%s&to=%s&dateFrom=%s&partner=picky";

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/query/getDateForUserInput")
    public String getDateForUserInput(@RequestParam("intent") String intent,
                                      @RequestParam("context") String context,
                                      @RequestParam("input") String input,
                                      @RequestParam(value="format", defaultValue="DD/MM/YYYY", required=false) String format){
        String dateStr = "21/08/2018";
        if(intent.trim().equals("flight_booking")){
            if(context.trim().equalsIgnoreCase("departure")){
                return "{\"set_attributes\":{\"departingOn\":\""+dateStr+"\"}}";
            }else if(context.trim().equalsIgnoreCase("arrival")){
                return "{\"set_attributes\":{\"returningOn\":\""+dateStr+"\"}}";
            }
        }

        return dateStr;
    }

    @RequestMapping("/query/flightbooking")
    public String queryForFlightBooking(@RequestParam("userId") String userId,
                                        @RequestParam("message") String message){
        AlterraTravelQuery query = new AlterraTravelQuery();
        query.setSession_id(userId);
        query.setMessage(message);
        query.setTimestamp(EpochConverter.getCurrentEpoch());

        HttpEntity<AlterraTravelQuery> request = new HttpEntity<>(query);
        ResponseEntity<String> response =
                restTemplate.postForEntity("https://alterra.ai/api/travel/", request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;

        try {
            root = mapper.readTree(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<JsonNode> itr = root.path("bot_replies").get(0).path("parts").elements();
        StringBuilder builder = new StringBuilder();
        String queryAttr;
        while (itr.hasNext()) {
            JsonNode node = itr.next();
            if(!node.path("link_url").isMissingNode()){
                //handle final line
                return findIternaries(root.path("bot_replies").get(0).path("parts"));
            }else{
                queryAttr = node.path("text").asText();
                //builder.append(node.toString());
                //builder.append(",");
                builder.append(queryAttr+" ");
            }
        }
        /*int length = builder.length();
        if (builder.lastIndexOf(",") == length - 1) {
            builder.deleteCharAt(length -1);
        }*/
        return String.format("{\"set_attributes\":{\"parsed Query\":\"%s\"}}",builder.toString());
        //return String.format("{\"messages\":{\"text\":\"%s\"}}",builder.toString());
        //return String.format("{ %s,\"messages\": [ %s ]}",setAttr, builder.toString());
    }

    private String findIternaries(JsonNode node){
        Iterator<JsonNode> itr = node.elements();

        String flyFrom ="";
        String flyTo = "";
        String departureOn = "";
        String returningOn = "";
        boolean oneway = false;

        while (itr.hasNext()) {
            JsonNode nodeJ = itr.next();
            if(!nodeJ.path("link_url").isMissingNode()){
                String line = nodeJ.path("link_url").asText();
                Pattern pattern =null;

                if(line.contains(";r=")) {
                    pattern = Pattern.compile("(.+)(#search;f=)(.*)(;t=)(.*)(;d=)(.*)(;r=)(.*)(;.*)");
                }else{
                    pattern = Pattern.compile("(.+)(#search;f=)(.*)(;t=)(.*)(;d=)(.*)(;tt=)(.*)(;.*)");
                    oneway = true;
                }

                Matcher matcher = pattern.matcher(line);
                while (matcher.find()){
                    flyFrom = matcher.group(3);
                    flyTo = matcher.group(5);
                    String[] fields = matcher.group(7).split("-");
                    departureOn = fields[2]+"/"+fields[1]+"/"+fields[0];
                    if(!oneway) {
                        fields = matcher.group(9).split("-");
                        returningOn = fields[2] + "/" + fields[1] + "/" + fields[0];
                    }
                }
                break;
            }
        }

        String searchFlightsUrl;
        if(oneway){
            searchFlightsUrl =  String.format(onewayFlightUrlTemplate,flyFrom,flyTo,departureOn);
        }else {
            searchFlightsUrl =  String.format(flightUrlTemplate, flyFrom, flyTo, departureOn, returningOn);
        }

        System.out.println("searchFlightsUrl is "+searchFlightsUrl);

        ResponseEntity<String> responseForCheapest =
                restTemplate.getForEntity(searchFlightsUrl+"&sort=price", String.class);
        System.out.println("responseForCheapest :::"+ responseForCheapest.getBody());
        ResponseEntity<String> responseForShortest =
                restTemplate.getForEntity(searchFlightsUrl+"&sort=duration", String.class);
        System.out.println("responseForShortest :::"+ responseForShortest.getBody());
        ResponseEntity<String> responseForPopular =
                restTemplate.getForEntity(searchFlightsUrl+"&sort=popularity", String.class);
        System.out.println("responseForPopular :::"+ responseForPopular.getBody());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootForCheapest=null, rootForShortest =null, rootForPopular = null;
        try {
            rootForCheapest = mapper.readTree(responseForCheapest.getBody());
            rootForShortest = mapper.readTree(responseForShortest.getBody());
            rootForPopular = mapper.readTree(responseForPopular.getBody());

        } catch (IOException e) {
            e.printStackTrace();
        }

        String cheapestFlight, shortestFlight, popularFlight;

        if(rootForCheapest.path("data").isMissingNode() || rootForCheapest.path("data").get(0) == null ){
            System.out.println("No flight search result found");
            cheapestFlight = "No data found for your search";
            shortestFlight = "No data found for your search";
            popularFlight = "No data found for your search";
        }else {
            cheapestFlight = rootForCheapest.path("data").get(0).path("deep_link").asText();
            shortestFlight = rootForShortest.path("data").get(0).path("deep_link").asText();
            popularFlight = rootForPopular.path("data").get(0).path("deep_link").asText();
        }

        System.out.println("cheapestFlight is "+cheapestFlight);
        System.out.println("shortestFlight is "+shortestFlight);
        System.out.println("popularFlight is "+popularFlight);

        return "{\"set_attributes\":{\"alterra chat ended\":\"true\",\"cheapest flight url\":\""+
                cheapestFlight+"\",\"shortest flight url\":\""+shortestFlight+"\",\"popular flight url\":\""+
                popularFlight+"\"}}";

        //return "{\"set_attributes\":{\"alterra chat ended\":\"true\"}}";
    }
}

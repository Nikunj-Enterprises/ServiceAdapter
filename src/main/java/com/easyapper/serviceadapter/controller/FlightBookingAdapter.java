package com.easyapper.serviceadapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
public class FlightBookingAdapter {
    String flightSearchResult = "{\n" +
            "    \"attachment\": {\n" +
            "      \"type\": \"template\",\n" +
            "      \"payload\": {\n" +
            "        \"template_type\": \"airline_itinerary\",\n" +
            "        \"intro_message\": \"Here's your flight itinerary.\",\n" +
            "        \"locale\": \"en_US\",\n" +
            "        \"pnr_number\": \"ABCDEF\",\n" +
            "        \"passenger_info\": [\n" +
            "          {\n" +
            "            \"name\": \"Farbound Smith Jr\",\n" +
            "            \"ticket_number\": \"0741234567890\",\n" +
            "            \"passenger_id\": \"p001\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"Nick Jones\",\n" +
            "            \"ticket_number\": \"0741234567891\",\n" +
            "            \"passenger_id\": \"p002\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"flight_info\": [\n" +
            "          {\n" +
            "            \"connection_id\": \"c001\",\n" +
            "            \"segment_id\": \"s001\",\n" +
            "            \"flight_number\": \"KL9123\",\n" +
            "            \"aircraft_type\": \"Boeing 737\",\n" +
            "            \"departure_airport\": {\n" +
            "              \"airport_code\": \"SFO\",\n" +
            "              \"city\": \"San Francisco\",\n" +
            "              \"terminal\": \"T4\",\n" +
            "              \"gate\": \"G8\"\n" +
            "            },\n" +
            "            \"arrival_airport\": {\n" +
            "              \"airport_code\": \"SLC\",\n" +
            "              \"city\": \"Salt Lake City\",\n" +
            "              \"terminal\": \"T4\",\n" +
            "              \"gate\": \"G8\"\n" +
            "            },\n" +
            "            \"flight_schedule\": {\n" +
            "              \"departure_time\": \"2016-01-02T19:45\",\n" +
            "              \"arrival_time\": \"2016-01-02T21:20\"\n" +
            "            },\n" +
            "            \"travel_class\": \"business\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"connection_id\": \"c002\",\n" +
            "            \"segment_id\": \"s002\",\n" +
            "            \"flight_number\": \"KL321\",\n" +
            "            \"aircraft_type\": \"Boeing 747-200\",\n" +
            "            \"travel_class\": \"business\",\n" +
            "            \"departure_airport\": {\n" +
            "              \"airport_code\": \"SLC\",\n" +
            "              \"city\": \"Salt Lake City\",\n" +
            "              \"terminal\": \"T1\",\n" +
            "              \"gate\": \"G33\"\n" +
            "            },\n" +
            "            \"arrival_airport\": {\n" +
            "              \"airport_code\": \"AMS\",\n" +
            "              \"city\": \"Amsterdam\",\n" +
            "              \"terminal\": \"T1\",\n" +
            "              \"gate\": \"G33\"\n" +
            "            },\n" +
            "            \"flight_schedule\": {\n" +
            "              \"departure_time\": \"2016-01-02T22:45\",\n" +
            "              \"arrival_time\": \"2016-01-03T17:20\"\n" +
            "            }\n" +
            "          }\n" +
            "        ],\n" +
            "        \"passenger_segment_info\": [\n" +
            "          {\n" +
            "            \"segment_id\": \"s001\",\n" +
            "            \"passenger_id\": \"p001\",\n" +
            "            \"seat\": \"12A\",\n" +
            "            \"seat_type\": \"Business\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"segment_id\": \"s001\",\n" +
            "            \"passenger_id\": \"p002\",\n" +
            "            \"seat\": \"12B\",\n" +
            "            \"seat_type\": \"Business\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"segment_id\": \"s002\",\n" +
            "            \"passenger_id\": \"p001\",\n" +
            "            \"seat\": \"73A\",\n" +
            "            \"seat_type\": \"World Business\",\n" +
            "            \"product_info\": [\n" +
            "              {\n" +
            "                \"title\": \"Lounge\",\n" +
            "                \"value\": \"Complimentary lounge access\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"title\": \"Baggage\",\n" +
            "                \"value\": \"1 extra bag 50lbs\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"segment_id\": \"s002\",\n" +
            "            \"passenger_id\": \"p002\",\n" +
            "            \"seat\": \"73B\",\n" +
            "            \"seat_type\": \"World Business\",\n" +
            "            \"product_info\": [\n" +
            "              {\n" +
            "                \"title\": \"Lounge\",\n" +
            "                \"value\": \"Complimentary lounge access\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"title\": \"Baggage\",\n" +
            "                \"value\": \"1 extra bag 50lbs\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        ],\n" +
            "        \"price_info\": [\n" +
            "          {\n" +
            "            \"title\": \"Fuel surcharge\",\n" +
            "            \"amount\": \"1597\",\n" +
            "            \"currency\": \"USD\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"base_price\": \"12206\",\n" +
            "        \"tax\": \"200\",\n" +
            "        \"total_price\": \"14003\",\n" +
            "        \"currency\": \"USD\"\n" +
            "      }\n" +
            "    }\n" +
            "  }";
    /**
     *
     * http://13.233.11.170:8091/flightbooking/searchflights?from={{flyingFromCity}}&to={{flyingToCity}}&departureDate={{departingOn}}&returnDate={{returningOn}}
     *
     * gives iternary list try to use template at http://pastebin.com/raw/HfBnQmHj
     */

    //https://api.skypicker.com/flights?from=bangalore&to=calcutta&dateFrom=20/08/2018&dateTo=20/08/2018&partner=picky&selectedAirlines=6E&selectedAirlinesExclude=false
    private final String flightUrlTemplate = "https://api.skypicker.com/flights?from=%s&to=%s&dateFrom=%s&dateTo=%s&partner=picky";

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/flightbooking/searchflights")
    public String searchFlights(@RequestParam("from") String flyingFromCity,
                                @RequestParam("to") String flyingToCity,
                                @RequestParam("departureDate") String departingOn,
                                @RequestParam("returnDate") String returningOn,
                                @RequestParam(value="selectedAirlines", required = false) String specificAirline,
                                @RequestParam(value="selectedAirlinesExclude", defaultValue = "false", required = false) Boolean isAirlineExcluded){
        // do some validation for request parameters
        String searchFlightsUrl =
                String.format(flightUrlTemplate,flyingFromCity,flyingToCity,departingOn,returningOn);
        ResponseEntity<String> response = restTemplate.getForEntity(searchFlightsUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.format("{\"messages\":[%s]}", flightSearchResult.replaceAll("\n",""));
    }
}

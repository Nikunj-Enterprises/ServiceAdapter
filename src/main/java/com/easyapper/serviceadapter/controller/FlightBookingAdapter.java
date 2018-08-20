package com.easyapper.serviceadapter.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightBookingAdapter {
    /**
     *
     * http://13.233.11.170:8091/queryparser/getDateForUserInput?intent=flight_booking&context=departure&input={{userDepartureTime}}
     *
     * returns set_attribute departingOn(dd/mm/yyyy)
     *
     * http://13.233.11.170:8091/queryparser/getDateForUserInput?intent=flight_booking&context=arrival&input={{userReturnDate}}
     *
     * returns set_attribute returningOn(dd/mm/yyyy), typeFlight=oneway/round
     *
     *
     * http://13.233.11.170:8091/flightbooking/searchflights?from={{flyingFromCity}}&to={{flyingToCity}}&departureDate={{departingOn}}&returnDate={{returningOn}}
     *
     * gives iternary list try to use template at http://pastebin.com/raw/HfBnQmHj
     */
}

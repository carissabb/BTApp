package com.example.btapp

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true) // to ignore the encapsulation (ie <CurrentRoutes>)

// class to get current route data
data class CurrentRoutesResponse(
    @JacksonXmlProperty(localName = "RouteName")
    val routeName: String? = null,

    @JacksonXmlProperty(localName = "RouteShortName")
    val routeShortName: String? = null,

    @JacksonXmlProperty(localName = "RouteColor")
    val routeColor: String? = null,

    @JacksonXmlProperty(localName = "RouteTextColor")
    val routeTextColor: String? = null,

    @JacksonXmlProperty(localName = "RealTimeInfoAvail")
    val realTimeInfoAvail: Boolean? = null
)

//data class CurrentRoutesResponse(
//    val routes: List<Route>
//)

//data class Route(
//    val routeName: String,
//    val routeShortName: String,
//    val routeColor: String,
//    val routeTextColor: String,
//    val realTimeInfoAvail: Boolean
//)



// class to get arrival and departure times for routes
data class ArrivalAndDepartureTimesForRoutesResponse(
    @JacksonXmlProperty(localName = "BlockID")
    val blockID: String? = null,

    @JacksonXmlProperty(localName = "TripID")
    val tripID: String? = null,

    @JacksonXmlProperty(localName = "StartTime")
    val startTime: String? = null,

    @JacksonXmlProperty(localName = "PatternName") // this is where short name goes (ie TCP)
    val patternName: String? = null,

    @JacksonXmlProperty(localName = "StopName")
    val stopName: String? = null,

    @JacksonXmlProperty(localName = "StopCode")
    val stopCode: String? = null,

    @JacksonXmlProperty(localName = "Rank")
    val rank: String? = null,

    @JacksonXmlProperty(localName = "IsTimePoint")
    val isTimePoint: String? = null,

    @JacksonXmlProperty(localName = "CalculatedArrivalTime")
    val calculatedArrivalTim: String? = null,

    @JacksonXmlProperty(localName = "CalculatedDepartureTime")
    val calculatedDepartureTime: String? = null

)
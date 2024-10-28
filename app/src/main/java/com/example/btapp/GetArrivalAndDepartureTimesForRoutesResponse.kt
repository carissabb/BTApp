package com.example.btapp

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

// this takes a string input of route short name and uses it to display the times for that route
// need to pass an on click from list of short names that leads to this



@JsonIgnoreProperties(ignoreUnknown = true) // to ignore the <DeparturesForRoute> encapsulation

// class to get current route data
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

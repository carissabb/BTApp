package com.example.btapp

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true) // to ignore the <CurrentRoutes> encapsulation

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
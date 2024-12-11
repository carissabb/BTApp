package com.example.btapp

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import android.os.Parcel
import android.os.Parcelable

/**
 * This this the file where the BT API responses are made into usable objects.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // to ignore the encapsulation (ie <CurrentRoutes>)

data class ScheduledStopCodesResponse(
    @JacksonXmlProperty(localName = "StopCode")
    val stopCode: String? = null,

    @JacksonXmlProperty(localName = "StopName")
    val stopName: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this (
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stopCode)
        parcel.writeString(stopName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduledStopCodesResponse> {
        override fun createFromParcel(parcel: Parcel): ScheduledStopCodesResponse {
            return ScheduledStopCodesResponse(parcel)
        }

        override fun newArray(size: Int): Array<ScheduledStopCodesResponse?> {
            return arrayOfNulls(size)
        }
    }
}
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
    val realTimeInfoAvail: Boolean? = null,

    @JacksonXmlProperty(localName = "Stability")
    val stability: String? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(routeName)
        parcel.writeString(routeShortName)
        parcel.writeString(routeColor)
        parcel.writeString(routeTextColor)
        parcel.writeValue(realTimeInfoAvail)
        parcel.writeString(stability)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentRoutesResponse> {
        override fun createFromParcel(parcel: Parcel): CurrentRoutesResponse {
            return CurrentRoutesResponse(parcel)
        }

        override fun newArray(size: Int): Array<CurrentRoutesResponse?> {
            return arrayOfNulls(size)
        }
    }
}


// class to get arrival and departure times for routes
@JsonIgnoreProperties(ignoreUnknown = true)
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
    val calculatedArrivalTime: String? = null,

    @JacksonXmlProperty(localName = "CalculatedDepartureTime")
    val calculatedDepartureTime: String? = null,

    @JacksonXmlProperty(localName = "RouteNotes")
    val routeNotes: String? = null

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(blockID)
        parcel.writeString(tripID)
        parcel.writeString(startTime)
        parcel.writeString(patternName)
        parcel.writeValue(stopName)
        parcel.writeValue(stopCode)
        parcel.writeValue(rank)
        parcel.writeValue(isTimePoint)
        parcel.writeValue(calculatedArrivalTime)
        parcel.writeValue(calculatedDepartureTime)
        parcel.writeValue(routeNotes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArrivalAndDepartureTimesForRoutesResponse> {
        override fun createFromParcel(parcel: Parcel): ArrivalAndDepartureTimesForRoutesResponse {
            return ArrivalAndDepartureTimesForRoutesResponse(parcel)
        }

        override fun newArray(size: Int): Array<ArrivalAndDepartureTimesForRoutesResponse?> {
            return arrayOfNulls(size)
        }
    }
}

// class to get scheduled routes form api
@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduledRoutesResponse(
    @JacksonXmlProperty(localName = "RouteName")
    val routeName: String? = null,

    @JacksonXmlProperty(localName = "RouteShortName")
    val routeShortName: String? = null,

    @JacksonXmlProperty(localName = "RouteColor")
    val routeColor: String? = null,

    @JacksonXmlProperty(localName = "RouteTextColor")
    val routeTextColor: String? = null,

    @JacksonXmlProperty(localName = "RouteURL")
    val routeURL: String? = null,

    @JacksonXmlProperty(localName = "ServiceLevel")
    val serviceLevel: String? = null

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(routeName)
        parcel.writeString(routeShortName)
        parcel.writeString(routeColor)
        parcel.writeString(routeTextColor)
        parcel.writeValue(routeURL)
        parcel.writeValue(serviceLevel)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduledRoutesResponse> {
        override fun createFromParcel(parcel: Parcel): ScheduledRoutesResponse {
            return ScheduledRoutesResponse(parcel)
        }

        override fun newArray(size: Int): Array<ScheduledRoutesResponse?> {
            return arrayOfNulls(size)
        }
    }
}

// Class to represent bus information from the API
data class BusInfo(
    @JacksonXmlProperty(localName = "AgencyVehicleName")
    val agencyVehicleName: String? = null,

    @JacksonXmlProperty(localName = "LatestEvent")
    val latestEvent: String? = null,

    @JacksonXmlProperty(localName = "Latitude")
    val latitude: Double? = null,

    @JacksonXmlProperty(localName = "Longitude")
    val longitude: Double? = null,

    @JacksonXmlProperty(localName = "Direction")
    val direction: Int? = null,

    @JacksonXmlProperty(localName = "Speed")
    val speed: String? = null,

    @JacksonXmlProperty(localName = "RouteShortName")
    val routeShortName: String? = null,

    @JacksonXmlProperty(localName = "BlockID")
    val blockID: String? = null,

    @JacksonXmlProperty(localName = "TripID")
    val tripID: String? = null,

    @JacksonXmlProperty(localName = "PatternName")
    val patternName: String? = null,

    @JacksonXmlProperty(localName = "TripStartTime")
    val tripStartTime: String? = null,

    @JacksonXmlProperty(localName = "LastStopName")
    val lastStopName: String? = null,

    @JacksonXmlProperty(localName = "StopCode")
    val stopCode: String? = null,

    @JacksonXmlProperty(localName = "Rank")
    val rank: String? = null,

    @JacksonXmlProperty(localName = "IsBusAtStop")
    val isBusAtStop: String? = null,

    @JacksonXmlProperty(localName = "IsTimePoint")
    val isTimePoint: String? = null,

    @JacksonXmlProperty(localName = "LatestRSAEvent")
    val latestRSAEvent: String? = null,

    @JacksonXmlProperty(localName = "TotalCount")
    val totalCount: String? = null,

    @JacksonXmlProperty(localName = "PercentOfCapacity")
    val percentOfCapacity: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(agencyVehicleName)
        parcel.writeString(latestEvent)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeValue(direction)
        parcel.writeString(speed)
        parcel.writeString(routeShortName)
        parcel.writeString(blockID)
        parcel.writeString(tripID)
        parcel.writeString(patternName)
        parcel.writeString(tripStartTime)
        parcel.writeString(lastStopName)
        parcel.writeString(stopCode)
        parcel.writeString(rank)
        parcel.writeString(isBusAtStop)
        parcel.writeString(isTimePoint)
        parcel.writeString(latestRSAEvent)
        parcel.writeString(totalCount)
        parcel.writeString(percentOfCapacity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BusInfo> {
        override fun createFromParcel(parcel: Parcel): BusInfo {
            return BusInfo(parcel)
        }

        override fun newArray(size: Int): Array<BusInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class AllPlacesResponse(
    @JacksonXmlProperty(localName = "PlaceID")
    val placeID: Int? = null,

    @JacksonXmlProperty(localName = "PlaceTypesID")
    val placeTypesID: Int? = null,

    @JacksonXmlProperty(localName = "Latitude")
    val latitude: Double? = null,

    @JacksonXmlProperty(localName = "Longitude")
    val longitude: Double? = null,

    @JacksonXmlProperty(localName = "PlaceName")
    val placeName: String? = null,

    @JacksonXmlProperty(localName = "Display")
    val display: Boolean? = null,

    @JacksonXmlProperty(localName = "Version")
    val version: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(placeID)
        parcel.writeValue(placeTypesID)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(placeName)
        parcel.writeValue(display)
        parcel.writeString(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentRoutesResponse> {
        override fun createFromParcel(parcel: Parcel): CurrentRoutesResponse {
            return CurrentRoutesResponse(parcel)
        }

        override fun newArray(size: Int): Array<CurrentRoutesResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class NearestStopsResponse(
    @JacksonXmlProperty(localName = "StopName")
    val stopName: String? = null,

    @JacksonXmlProperty(localName = "StopCode")
    val stopCode: Int? = null,

    @JacksonXmlProperty(localName = "Feet")
    val feet: Double? = null,

    @JacksonXmlProperty(localName = "Miles")
    val miles: Double? = null,

    @JacksonXmlProperty(localName = "Latitude")
    val latitude: Double? = null,

    @JacksonXmlProperty(localName = "Longitude")
    val longitude: Double? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Double
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stopName)
        parcel.writeValue(stopCode)
        parcel.writeValue(feet)
        parcel.writeValue(miles)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentRoutesResponse> {
        override fun createFromParcel(parcel: Parcel): CurrentRoutesResponse {
            return CurrentRoutesResponse(parcel)
        }

        override fun newArray(size: Int): Array<CurrentRoutesResponse?> {
            return arrayOfNulls(size)
        }
    }
}

package com.example.btapp

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GetCurrentRoutesResponse(
    @Json(name = "GetCurrentRoutesResult")  // map to Json key
    val routes: String // holds the raw XML response
)

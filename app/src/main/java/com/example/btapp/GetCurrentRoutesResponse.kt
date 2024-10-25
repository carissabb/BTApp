package com.example.btapp

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "GetCurrentRoutesResponse")
data class GetCurrentRoutesResponse(
    @field:XmlElement(name = "GetCurrentRoutesResult")
    var result: String? = null
)


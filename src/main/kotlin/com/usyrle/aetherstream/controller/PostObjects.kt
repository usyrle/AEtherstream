package com.usyrle.aetherstream.controller

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GenerateRequest(
    var size: Int?,
    var phenomena: Boolean?
)

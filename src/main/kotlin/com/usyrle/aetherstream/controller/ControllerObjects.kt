package com.usyrle.aetherstream.controller

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.usyrle.aetherstream.repo.PlanarCard

@JsonIgnoreProperties(ignoreUnknown = true)
data class GenerateRequest(
    var size: Int?,
    var phenomena: Boolean?
)

data class PlanarDeckInfo(
    var deckSize: Int,
    var currentPlane: PlanarCard,
    var spatialMergingPlane: PlanarCard? = null,
    var startTime: Long,
    var id: String
)

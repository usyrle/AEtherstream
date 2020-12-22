package com.usyrle.aetherstream.controller

import com.usyrle.aetherstream.service.PlaneService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PlaneController(private val service: PlaneService) {

    @PostMapping(path = ["/plane/generate"], consumes = ["application/json"])
    fun generateNewPlaneDeck(
        @RequestBody request: GenerateRequest
    ) =
        service.generatePlanarDeck(request.size ?: 10, request.phenomena ?: true)
}

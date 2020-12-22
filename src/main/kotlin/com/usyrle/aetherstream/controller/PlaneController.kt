package com.usyrle.aetherstream.controller

import com.usyrle.aetherstream.repo.PlanarDeckRepository
import com.usyrle.aetherstream.service.PlaneService
import org.springframework.web.bind.annotation.*

@RestController
class PlaneController(private val service: PlaneService, private val repo: PlanarDeckRepository) {

    @PostMapping(path = ["/deck/generate"], consumes = ["application/json"])
    fun generateNewPlanarDeck(
        @RequestBody request: GenerateRequest
    ) =
        service.generatePlanarDeck(request.size ?: 10, request.phenomena ?: true)

    @GetMapping("/deck/{deckId}")
    fun getPlanarDeckInfo(
        @PathVariable deckId: String
    ) =
        repo.findById(deckId)
}

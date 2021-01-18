package com.usyrle.aetherstream.controller

import com.usyrle.aetherstream.repo.PlanarDeckRepository
import com.usyrle.aetherstream.service.PlaneService
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/deck")
class PlaneController(private val service: PlaneService, private val repo: PlanarDeckRepository) {

    @PostMapping(path = ["/generate"], consumes = ["application/json"])
    fun generateNewPlanarDeck(
        @RequestBody request: GenerateRequest
    ): PlanarDeckInfo {
        val deckSize = decideDeckSize(request.size ?: 10, request.phenomena ?: true)
        val newDeck = service.generatePlanarDeck(deckSize, request.phenomena ?: true)

        return PlanarDeckInfo(
            deckSize = newDeck.cards.size + 1,
            currentPlane = newDeck.currentCard,
            startTime = newDeck.startTime.toInstant().epochSecond,
            id = newDeck.id ?: "00000000"
        )
    }

    @GetMapping("/{deckId}")
    fun getPlanarDeckInfo(
        @PathVariable deckId: String
    ): PlanarDeckInfo? {
        val result = repo.findById(deckId)

        if (result.isPresent) {
            val requestedDeck = result.get()
            return PlanarDeckInfo(
                deckSize = requestedDeck.cards.size + 1,
                currentPlane = requestedDeck.currentCard,
                spatialMergingPlane = requestedDeck.spatialMergingCard,
                startTime = requestedDeck.startTime.toInstant().epochSecond,
                id = requestedDeck.id ?: "00000000"
            )
        }
        return null
    }

    @PostMapping("/{deckId}/next")
    fun playNextPlanarCard(
        @PathVariable deckId: String
    ): PlanarDeckInfo? {
        val result = repo.findById(deckId)

        if (result.isEmpty) {
            return null
        }

        val deck = result.get()
        val updatedDeck = service.playNextPlanarCard(deck);

        return PlanarDeckInfo(
            deckSize = updatedDeck.cards.size + 1,
            currentPlane = updatedDeck.currentCard,
            spatialMergingPlane = updatedDeck.spatialMergingCard,
            startTime = updatedDeck.startTime.toInstant().epochSecond,
            id = updatedDeck.id ?: "00000000"
        )
    }
}

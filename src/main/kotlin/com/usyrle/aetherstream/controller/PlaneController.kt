package com.usyrle.aetherstream.controller

import com.usyrle.aetherstream.repo.PlanarDeck
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
        val newDeck = service.generatePlanarDeck(request.size ?: 10, request.phenomena ?: true)

        return PlanarDeckInfo(
            deckSize = newDeck.cards.size,
            currentPlane = newDeck.cards[0],
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
                deckSize = requestedDeck.cards.size,
                currentPlane = requestedDeck.cards[requestedDeck.currentIndex],
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

        if (result.isPresent) {
            val deck = result.get()
            val nextPlaneIndex = if (deck.currentIndex + 1 >= deck.cards.size) 0 else deck.currentIndex + 1

            repo.save(
                PlanarDeck(
                    cards = deck.cards,
                    startTime = deck.startTime,
                    currentIndex = nextPlaneIndex,
                    id = deck.id
                )
            )

            return PlanarDeckInfo(
                deckSize = deck.cards.size,
                currentPlane = deck.cards[nextPlaneIndex],
                startTime = deck.startTime.toInstant().epochSecond,
                id = deck.id ?: "00000000"
            )
        }
        return null
    }
}

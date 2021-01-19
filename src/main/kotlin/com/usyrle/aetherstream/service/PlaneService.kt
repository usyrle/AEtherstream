package com.usyrle.aetherstream.service

import com.usyrle.aetherstream.repo.PlanarCard
import com.usyrle.aetherstream.repo.PlanarCardRepository
import com.usyrle.aetherstream.repo.PlanarDeck
import com.usyrle.aetherstream.repo.PlanarDeckRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

const val PLANE_TYPE = "plane"
const val PHENOM_TYPE = "phenomenon"

const val MILLISECONDS_IN_24H = 86400000
const val SPATIAL_MERGING_ID: Long = 423588
const val INTERPLANAR_TUNNEL_ID: Long = 423583

@Service
class PlaneService(
    private val planarCardRepository: PlanarCardRepository,
    private val planarDeckRepository: PlanarDeckRepository
) {

    fun generatePlanarDeck(deckSize: Int, usePhenomenon: Boolean): PlanarDeck {
        if (deckSize < 10) throw IllegalArgumentException("planar deck must be at least 10 cards")

        val planes = planarCardRepository.findAllByType(PLANE_TYPE).shuffled()

        if (usePhenomenon) {
            val phenomena = planarCardRepository.findAllByType(PHENOM_TYPE).shuffled()

            val generatedPlaneList = (planes.take(deckSize - 3) + phenomena.take(2))
                .shuffled()
                .toMutableList()

            return planarDeckRepository.save(
                PlanarDeck(
                    cards = generatedPlaneList,
                    currentCard = planes.last()
                )
            )
        }

        return planarDeckRepository.save(
            PlanarDeck(
                cards = planes.take(deckSize - 1).toMutableList(),
                currentCard = planes.last()
            )
        )
    }

    fun playNextPlanarCard(deck: PlanarDeck, chosenInterplanarCardId: Long? = null): PlanarDeck {
        if (deck.currentCard.multiverseId == SPATIAL_MERGING_ID) {
            // handle Spatial Merging phenomenon
            val spatialMergingPlane = dealToNextPlane(deck)
            val nextPlane = dealToNextPlane(deck)

            deck.cards.add(deck.currentCard)

            return planarDeckRepository.save(
                PlanarDeck(
                    cards = deck.cards,
                    startTime = deck.startTime,
                    spatialMergingCard = spatialMergingPlane,
                    currentCard = nextPlane,
                    id = deck.id
                )
            )
        } else if (deck.currentCard.multiverseId == INTERPLANAR_TUNNEL_ID) {
            // handle Interplanar Tunnel phenomenon
            if (chosenInterplanarCardId != null) {
                val chosenCard = planarCardRepository.findById(chosenInterplanarCardId)
                deck.cards.remove(chosenCard.get())

                deck.cards = (deck.cards + (deck.interplanarCards!!.shuffled())).toMutableList()
                deck.cards.add(deck.currentCard)

                return planarDeckRepository.save(
                    PlanarDeck(
                        cards = deck.cards,
                        startTime = deck.startTime,
                        currentCard = chosenCard.get(),
                        id = deck.id
                    )
                )
            }

            val interplanarChoices = mutableListOf<PlanarCard>()
            var planeCount = 0

            while (planeCount < 5) {
                val nextCard = deck.cards[0]
                interplanarChoices.add(nextCard)
                deck.cards.remove(nextCard)
                if (nextCard.type == "plane") planeCount++
            }

            return planarDeckRepository.save(
                PlanarDeck(
                    cards = deck.cards,
                    startTime = deck.startTime,
                    interplanarCards = interplanarChoices,
                    currentCard = deck.currentCard,
                    id = deck.id
                )
            )
        }

        if (deck.spatialMergingCard != null) {
            deck.cards.add(deck.spatialMergingCard!!)
        }

        val nextPlane = deck.cards[0]
        deck.cards.remove(nextPlane)
        deck.cards.add(deck.currentCard)

        return planarDeckRepository.save(
            PlanarDeck(
                cards = deck.cards,
                startTime = deck.startTime,
                currentCard = nextPlane,
                id = deck.id
            )
        )
    }

    private fun dealToNextPlane(deck: PlanarDeck): PlanarCard {
        var nextPlane: PlanarCard? = null

        while (nextPlane == null) {
            val tempPlane = deck.cards[0]
            if (tempPlane.type != "plane") {
                deck.cards.remove(tempPlane)
                deck.cards.add(tempPlane)
            } else {
                nextPlane = tempPlane
                deck.cards.remove(tempPlane)
            }
        }

        return nextPlane
    }

    @Scheduled(cron = "\${deck.prune.schedule}")
    fun pruneOldPlanarDecks() {
        val deckList = planarDeckRepository.findAll()
        val current = Date()

        for (deck in deckList) {
            if (current.time - deck.startTime.time > MILLISECONDS_IN_24H) {
                planarDeckRepository.delete(deck)
            }
        }
    }
}

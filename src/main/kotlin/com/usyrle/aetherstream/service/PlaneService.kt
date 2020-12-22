package com.usyrle.aetherstream.service

import com.usyrle.aetherstream.repo.PlanarDeck
import com.usyrle.aetherstream.repo.PlaneCardRepository
import org.springframework.stereotype.Service

const val PLANE_TYPE = "plane"
const val PHENOM_TYPE = "phenomenon"

@Service
class PlaneService(private val repository: PlaneCardRepository) {

    fun generatePlanarDeck(deckSize: Int, usePhenomenon: Boolean): PlanarDeck {
        if (deckSize < 10) throw IllegalArgumentException("planar deck must be at least 10 cards")

        val planes = repository.findAllByType(PLANE_TYPE).shuffled()

        if (usePhenomenon) {
            val phenomena = repository.findAllByType(PHENOM_TYPE).shuffled()

            val generatedPlaneList = listOf(planes.last()) +
                    (planes.subList(0, deckSize - 3) + phenomena.subList(0, 2))
                            .shuffled()

            return PlanarDeck(generatedPlaneList, null)
        }

        return PlanarDeck(planes.subList(0, deckSize), null)
    }
}

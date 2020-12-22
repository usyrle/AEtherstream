package com.usyrle.aetherstream.service

import com.usyrle.aetherstream.repo.PlanarDeck
import com.usyrle.aetherstream.repo.PlanarCardRepository
import com.usyrle.aetherstream.repo.PlanarDeckRepository
import org.springframework.stereotype.Service

const val PLANE_TYPE = "plane"
const val PHENOM_TYPE = "phenomenon"

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

            generatedPlaneList.add(0, planes.last())

            return planarDeckRepository.save(PlanarDeck(generatedPlaneList))
        }

        return planarDeckRepository.save(PlanarDeck(planes.take(deckSize).toMutableList()))
    }
}

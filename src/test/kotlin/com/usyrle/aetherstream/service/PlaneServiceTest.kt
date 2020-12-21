package com.usyrle.aetherstream.service

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.usyrle.aetherstream.repo.PlaneCard
import com.usyrle.aetherstream.repo.PlaneCardRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PlaneServiceTest {

    val mockRepository: PlaneCardRepository = mock()
    val testPlanes: List<PlaneCard> = listOf(
            PlaneCard("Plane1", "plane", "https://api.scryfall.com", 12341),
            PlaneCard("Plane2", "plane", "https://api.scryfall.com", 12342),
            PlaneCard("Plane3", "plane", "https://api.scryfall.com", 12343),
            PlaneCard("Plane4", "plane", "https://api.scryfall.com", 12344),
            PlaneCard("Plane5", "plane", "https://api.scryfall.com", 12345),
            PlaneCard("Plane6", "plane", "https://api.scryfall.com", 12346),
            PlaneCard("Plane7", "plane", "https://api.scryfall.com", 12347),
            PlaneCard("Plane8", "plane", "https://api.scryfall.com", 12348),
            PlaneCard("Plane9", "plane", "https://api.scryfall.com", 12349),
            PlaneCard("Plane10", "plane", "https://api.scryfall.com", 123410),
            PlaneCard("Plane11", "plane", "https://api.scryfall.com", 123411),
            PlaneCard("Plane12", "plane", "https://api.scryfall.com", 123412)
    )
    val testPhenomena: List<PlaneCard> = listOf(
            PlaneCard("Phenomenon1", "phenomenon", "https://api.scryfall.com", 99991),
            PlaneCard("Phenomenon2", "phenomenon", "https://api.scryfall.com", 99992),
            PlaneCard("Phenomenon3", "phenomenon", "https://api.scryfall.com", 99993),
            PlaneCard("Phenomenon4", "phenomenon", "https://api.scryfall.com", 99994),
            PlaneCard("Phenomenon5", "phenomenon", "https://api.scryfall.com", 99995),
            PlaneCard("Phenomenon6", "phenomenon", "https://api.scryfall.com", 99996)
    )

    @Test
    fun generatePlaneSet_createsSetWithExactlyEightRandomPlanesAndTwoRandomPhenomena() {
        val subject = PlaneService(mockRepository)

        whenever(mockRepository.findAllByType("plane")).thenReturn(testPlanes)
        whenever(mockRepository.findAllByType("phenomenon")).thenReturn(testPhenomena)

        val actual: List<PlaneCard> = subject.generatePlaneSet().cards

        // check that elements are unique
        assertThat(actual).containsExactlyElementsOf(actual.toSet())

        assertThat(actual).filteredOn("type", "plane").hasSize(8)
        assertThat(actual).filteredOn("type", "phenomenon").hasSize(2)
    }

    @Test
    fun generatePlaneSet_firstElementIsNeverAPhenomenon() {
        val subject = PlaneService(mockRepository)

        whenever(mockRepository.findAllByType("plane")).thenReturn(testPlanes)
        whenever(mockRepository.findAllByType("phenomenon")).thenReturn(testPhenomena)

        val actualFirstElements: MutableList<PlaneCard> = mutableListOf()

        for (i in 1..100) {
            val actual: List<PlaneCard> = subject.generatePlaneSet().cards
            actualFirstElements.add(actual[0])
        }

        assertThat(actualFirstElements).filteredOn("type", "phenomenon").isEmpty()
    }
}

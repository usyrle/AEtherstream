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

    @Test
    fun generatePlaneSet_createsSetWithExactlyTenRandomPlanes() {
        val subject = PlaneService(mockRepository)

        whenever(mockRepository.findAllByType("plane")).thenReturn(testPlanes)

        val actual: List<PlaneCard> = subject.generatePlaneSet().cards

        assertThat(actual).hasSize(10)

        // check that elements are unique
        assertThat(actual).containsExactlyElementsOf(actual.toSet())

        // yes, this can fail if i'm unlucky; it's rare enough that i'll live
        assertThat(actual).isNotEqualTo(testPlanes.subList(0, 10))
    }
}

package com.usyrle.aetherstream.controller

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.usyrle.aetherstream.repo.PlanarCard
import com.usyrle.aetherstream.repo.PlanarDeck
import com.usyrle.aetherstream.repo.PlanarDeckRepository
import com.usyrle.aetherstream.service.PlaneService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

internal class PlaneControllerTest {

    @Mock
    lateinit var mockPlaneService: PlaneService

    @Mock
    lateinit var mockRepository: PlanarDeckRepository

    @InjectMocks
    lateinit var subject: PlaneController

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)

        whenever(mockPlaneService.generatePlanarDeck(any(), any())).thenReturn(
            PlanarDeck(
                cards = mutableListOf(
                    PlanarCard("Plane1", "plane", "https://api.scryfall.com", 12341)
                ),
                startTime = Date()
            )
        )
    }

    @Test
    fun generateNewPlanarDeck_generatesWithRequestedValues() {
        subject.generateNewPlanarDeck(GenerateRequest(20, false))

        verify(mockPlaneService).generatePlanarDeck(20, false)
    }

    @Test
    fun generateNewPlanarDeck_defaultsToTenCardsAndUsePhenomena() {
        subject.generateNewPlanarDeck(GenerateRequest(null, null))

        verify(mockPlaneService).generatePlanarDeck(10, true)
    }

    @Test
    fun generateNewPlanarDeck_returnsBasicInfoAboutDeck() {
        val testCards = mutableListOf(
            PlanarCard("Plane1", "plane", "https://api.scryfall.com", 12341),
            PlanarCard("Plane2", "plane", "https://api.scryfall.com", 12342),
            PlanarCard("Plane3", "plane", "https://api.scryfall.com", 12343)
        )
        val testDeck = PlanarDeck(
            cards = testCards,
            startTime = Date(),
            currentIndex = 0,
            id = "TEST"
        )

        whenever(mockPlaneService.generatePlanarDeck(any(), any())).thenReturn(testDeck)

        val actual: PlanarDeckInfo = subject.generateNewPlanarDeck(GenerateRequest(null, null))

        assertThat(actual.deckSize).isEqualTo(testCards.size)
        assertThat(actual.startTime).isEqualTo(testDeck.startTime.toInstant().epochSecond)
        assertThat(actual.currentPlane).isEqualTo(testCards[0])
        assertThat(actual.id).isEqualTo(testDeck.id)
    }

    @Test
    fun getPlanarDeckInfo_returnsBasicInfoAboutDeck() {
        val testCards = mutableListOf(
            PlanarCard("Plane1", "plane", "https://api.scryfall.com", 12341),
            PlanarCard("Plane2", "plane", "https://api.scryfall.com", 12342),
            PlanarCard("Plane3", "plane", "https://api.scryfall.com", 12343)
        )
        val testDeck = PlanarDeck(
            cards = testCards,
            startTime = Date(),
            currentIndex = 2,
            id = "TEST"
        )

        whenever(mockRepository.findById("TEST")).thenReturn(Optional.of(testDeck))

        val actual = subject.getPlanarDeckInfo("TEST")

        assertThat(actual!!.deckSize).isEqualTo(testCards.size)
        assertThat(actual.startTime).isEqualTo(testDeck.startTime.toInstant().epochSecond)
        assertThat(actual.currentPlane).isEqualTo(testCards[2])
        assertThat(actual.id).isEqualTo(testDeck.id)
    }

    @Test
    fun getPlanarDeckInfo_returnsNullIfNotFound() {
        whenever(mockRepository.findById("TEST")).thenReturn(Optional.empty())

        val actual = subject.getPlanarDeckInfo("TEST")

        assertThat(actual).isNull()
    }
}

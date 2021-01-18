package com.usyrle.aetherstream.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.usyrle.aetherstream.repo.PlanarCard
import com.usyrle.aetherstream.repo.PlanarCardRepository
import com.usyrle.aetherstream.repo.PlanarDeck
import com.usyrle.aetherstream.repo.PlanarDeckRepository
import org.apache.commons.lang3.time.DateUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations.initMocks
import java.util.*
import kotlin.test.assertFailsWith

internal class PlaneServiceTest {

    val testPlanes: List<PlanarCard> = listOf(
            PlanarCard("Plane1", "plane", "https://api.scryfall.com", 12341),
            PlanarCard("Plane2", "plane", "https://api.scryfall.com", 12342),
            PlanarCard("Plane3", "plane", "https://api.scryfall.com", 12343),
            PlanarCard("Plane4", "plane", "https://api.scryfall.com", 12344),
            PlanarCard("Plane5", "plane", "https://api.scryfall.com", 12345),
            PlanarCard("Plane6", "plane", "https://api.scryfall.com", 12346),
            PlanarCard("Plane7", "plane", "https://api.scryfall.com", 12347),
            PlanarCard("Plane8", "plane", "https://api.scryfall.com", 12348),
            PlanarCard("Plane9", "plane", "https://api.scryfall.com", 12349),
            PlanarCard("Plane10", "plane", "https://api.scryfall.com", 123410),
            PlanarCard("Plane11", "plane", "https://api.scryfall.com", 123411),
            PlanarCard("Plane12", "plane", "https://api.scryfall.com", 123412),
            PlanarCard("Plane13", "plane", "https://api.scryfall.com", 123412),
            PlanarCard("Plane14", "plane", "https://api.scryfall.com", 123412),
            PlanarCard("Plane15", "plane", "https://api.scryfall.com", 123412),
            PlanarCard("Plane16", "plane", "https://api.scryfall.com", 123412)
    )
    val testPhenomena: List<PlanarCard> = listOf(
            PlanarCard("Phenomenon1", "phenomenon", "https://api.scryfall.com", 99991),
            PlanarCard("Phenomenon2", "phenomenon", "https://api.scryfall.com", 99992),
            PlanarCard("Phenomenon3", "phenomenon", "https://api.scryfall.com", 99993),
            PlanarCard("Phenomenon4", "phenomenon", "https://api.scryfall.com", 99994),
            PlanarCard("Phenomenon5", "phenomenon", "https://api.scryfall.com", 99995),
            PlanarCard("Phenomenon6", "phenomenon", "https://api.scryfall.com", 99996)
    )

    @Captor
    lateinit var deckCaptor: ArgumentCaptor<PlanarDeck>
    @Mock
    lateinit var mockCardRepository: PlanarCardRepository
    @Mock
    lateinit var mockDeckRepository: PlanarDeckRepository
    @InjectMocks
    lateinit var subject: PlaneService

    @BeforeEach
    fun setup() {
        initMocks(this)

        whenever(mockCardRepository.findAllByType("plane")).thenReturn(testPlanes)

        whenever(mockDeckRepository.save(any<PlanarDeck>())).thenAnswer { it.arguments[0] }
    }

    @Test
    fun generatePlanarDeck_createsDeckOfRequestedSizeAndTwoPhenomenon() {
        whenever(mockCardRepository.findAllByType("phenomenon")).thenReturn(testPhenomena)

        val actual: List<PlanarCard> = subject.generatePlanarDeck(12, true).cards

        // check that elements are unique
        assertThat(actual).containsExactlyElementsOf(actual.toSet())

        assertThat(actual).hasSize(12)
        assertThat(actual).filteredOn("type", "plane").hasSize(10)
        assertThat(actual).filteredOn("type", "phenomenon").hasSize(2)
    }

    @Test
    fun generatePlanarDeck_savesGeneratedDeck() {
        whenever(mockCardRepository.findAllByType("phenomenon")).thenReturn(testPhenomena)

        val actual = subject.generatePlanarDeck(12, true)

        verify(mockDeckRepository).save(actual)
    }

    @Test
    fun generatePlanarDeck_createsDeckWithoutPhenomenaWhenRequested() {
        val actual: List<PlanarCard> = subject.generatePlanarDeck(16, false).cards

        // check that elements are unique
        assertThat(actual).containsExactlyElementsOf(actual.toSet())

        assertThat(actual).filteredOn("type", "plane").hasSize(16)
        assertThat(actual).filteredOn("type", "phenomenon").hasSize(0)
    }

    @Test
    fun generatePlanarDeck_throwsErrorIfDeckSizeIsLessThanTen() {
        assertFailsWith<IllegalArgumentException> {
            subject.generatePlanarDeck(2, false)
        }
    }

    @Test
    fun generatePlanarDeck_firstElementIsNeverAPhenomenon() {
        whenever(mockCardRepository.findAllByType("phenomenon")).thenReturn(testPhenomena)

        val actualFirstElements: MutableList<PlanarCard> = mutableListOf()

        for (i in 1..100) {
            val actual: List<PlanarCard> = subject.generatePlanarDeck(10, true).cards
            actualFirstElements.add(actual[0])
        }

        assertThat(actualFirstElements).filteredOn("type", "phenomenon").isEmpty()
    }

    @Test
    fun playNextPlanarCard_updatesDatabaseWithNewIndex() {
        val testDeck = PlanarDeck(
            cards = testPlanes.toMutableList(),
            startTime = Date(),
            currentIndex = 0,
            id = "TEST"
        )

        subject.playNextPlanarCard(testDeck)

        verify(mockDeckRepository).save(deckCaptor.capture())

        val actual = deckCaptor.value

        assertThat(actual.cards).isEqualTo(testDeck.cards)
        assertThat(actual.startTime).isEqualTo(testDeck.startTime)
        assertThat(actual.currentIndex).isEqualTo(testDeck.currentIndex + 1)
        assertThat(actual.id).isEqualTo(testDeck.id)
    }

    @Test
    fun playNextPlanarCard_indexRollsOverWhenListEndIsReached() {
        val testDeck = PlanarDeck(
            cards = testPlanes.toMutableList(),
            startTime = Date(),
            currentIndex = testPlanes.size - 1,
            id = "TEST"
        )

        subject.playNextPlanarCard(testDeck)

        verify(mockDeckRepository).save(PlanarDeck(
            cards = testDeck.cards,
            startTime = testDeck.startTime,
            currentIndex = 0,
            id = testDeck.id
        ))
    }

    @Test
    fun pruneOldPlanarDecks_removeAllDecksOlderThan24Hours() {
        val testDecks = listOf(
                PlanarDeck(mutableListOf(), Date(), 0, "abcdef" ),
                PlanarDeck(mutableListOf(), DateUtils.addHours(Date(), -4), 0, "ghijkl"),
                PlanarDeck(mutableListOf(), DateUtils.addHours(Date(), -50), 0, "mnopqr"),
                PlanarDeck(mutableListOf(), DateUtils.addDays(Date(), -10), 0, "stuvwx"),
                PlanarDeck(mutableListOf(), DateUtils.addMonths(Date(), -1), 0, "yabbaz")
        )

        whenever(mockDeckRepository.findAll()).thenReturn(testDecks)

        subject.pruneOldPlanarDecks()

        verify(mockDeckRepository, never()).delete(testDecks[0])
        verify(mockDeckRepository, never()).delete(testDecks[1])

        verify(mockDeckRepository).delete(testDecks[2])
        verify(mockDeckRepository).delete(testDecks[3])
        verify(mockDeckRepository).delete(testDecks[4])
    }
}

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

        val actual = subject.generatePlanarDeck(12, true)

        // check that elements are unique
        assertThat(actual.cards).containsExactlyElementsOf(actual.cards.toSet())

        assertThat(actual.cards).hasSize(12 - 1)
        assertThat(actual.cards).filteredOn("type", "plane").hasSizeLessThanOrEqualTo(10)
        assertThat(actual.cards).filteredOn("type", "phenomenon").hasSizeLessThanOrEqualTo(2)

        assertThat(actual.currentCard).isNotNull
    }

    @Test
    fun generatePlanarDeck_savesGeneratedDeck() {
        whenever(mockCardRepository.findAllByType("phenomenon")).thenReturn(testPhenomena)

        val actual = subject.generatePlanarDeck(12, true)

        verify(mockDeckRepository).save(actual)
    }

    @Test
    fun generatePlanarDeck_createsDeckWithoutPhenomenaWhenRequested() {
        val actual = subject.generatePlanarDeck(16, false)

        // check that elements are unique
        assertThat(actual.cards).containsExactlyElementsOf(actual.cards.toSet())

        assertThat(actual.cards).filteredOn("type", "plane").hasSizeLessThanOrEqualTo(16)
        assertThat(actual.cards).filteredOn("type", "phenomenon").isEmpty()

        assertThat(actual.currentCard.type).isEqualTo("plane")
    }

    @Test
    fun generatePlanarDeck_throwsErrorIfDeckSizeIsLessThanTen() {
        assertFailsWith<IllegalArgumentException> {
            subject.generatePlanarDeck(2, false)
        }
    }

    @Test
    fun generatePlanarDeck_currentCardIsNeverAPhenomenon() {
        whenever(mockCardRepository.findAllByType("phenomenon")).thenReturn(testPhenomena)

        val actualFirstElements: MutableList<PlanarCard> = mutableListOf()

        for (i in 1..100) {
            val actual = subject.generatePlanarDeck(10, true)
            actualFirstElements.add(actual.currentCard)
        }

        assertThat(actualFirstElements).filteredOn("type", "phenomenon").isEmpty()
    }

    @Test
    fun generatePlanarDeck_currentCardIsNeverInCardsList() {
        for (i in 1..100) {
            val actual = subject.generatePlanarDeck(10, false)
            assertThat(actual.cards).doesNotContain(actual.currentCard)
        }
    }

    @Test
    fun playNextPlanarCard_updatesDatabaseWithNewCurrentCard() {
        val testCurrentPlane = PlanarCard(
            "CurrentPlane",
            "plane",
            "https://api.scryfall.com",
            99999
        )
        val testDeck = PlanarDeck(
            cards = testPlanes.toMutableList(),
            startTime = Date(),
            currentCard = testCurrentPlane,
            id = "TEST"
        )

        subject.playNextPlanarCard(testDeck)

        verify(mockDeckRepository).save(deckCaptor.capture())
        val actual = deckCaptor.value

        assertThat(actual.startTime).isEqualTo(testDeck.startTime)
        assertThat(actual.currentCard).isEqualTo(testPlanes[0])
        assertThat(actual.cards).doesNotContain(actual.currentCard)
        assertThat(actual.id).isEqualTo(testDeck.id)
    }

    @Test
    fun playNextPlanarCard_pushesPreviousCardToEndOfCardsList() {
        val testCurrentPlane = PlanarCard(
            "CurrentPlane",
            "plane",
            "https://api.scryfall.com",
            99999
        )
        val testDeck = PlanarDeck(
            cards = testPlanes.toMutableList(),
            startTime = Date(),
            currentCard = testCurrentPlane,
            id = "TEST"
        )

        subject.playNextPlanarCard(testDeck)

        verify(mockDeckRepository).save(deckCaptor.capture())
        val actual = deckCaptor.value

        assertThat(actual.cards.last()).isEqualTo(testCurrentPlane)
    }

    @Test
    fun playNextPlanarCard_spatialMerging_populatesSpatialMergingPlane() {
        val spatialMerging = PlanarCard(
            "Spatial Merging",
            "phenomenon",
            "https://scryfall.com/card/opca/7/spatial-merging",
            423588
        )
        val testDeck = PlanarDeck(
            cards = testPlanes.toMutableList(),
            startTime = Date(),
            currentCard = spatialMerging,
            id = "TEST"
        )

        subject.playNextPlanarCard(testDeck)

        verify(mockDeckRepository).save(deckCaptor.capture())
        val actual = deckCaptor.value

        assertThat(actual.spatialMergingCard).isEqualTo(testPlanes[0])
        assertThat(actual.currentCard).isEqualTo(testPlanes[1])
        assertThat(actual.cards).doesNotContainAnyElementsOf(
            listOf(actual.currentCard, actual.spatialMergingCard)
        )
    }

    @Test
    fun playNextPlanarCard_spatialMerging_pushesNonPlaneCardsToBottomOfDeckWhenDeterminingSpatialMergingPlane() {
        val spatialMerging = PlanarCard(
            "Spatial Merging",
            "phenomenon",
            "https://scryfall.com/card/opca/7/spatial-merging",
            423588
        )
        val testCards = mutableListOf(
            PlanarCard("Plane1", "plane", "https://api.scryfall.com", 12341),
            PlanarCard("Phenomenon1", "phenomenon", "https://api.scryfall.com", 99991),
            PlanarCard("Phenomenon2", "phenomenon", "https://api.scryfall.com", 99992),
            PlanarCard("Plane2", "plane", "https://api.scryfall.com", 12342),
            PlanarCard("Plane3", "plane", "https://api.scryfall.com", 12343)
        )
        val testDeck = PlanarDeck(
            cards = testCards,
            startTime = Date(),
            currentCard = spatialMerging,
            id = "TEST"
        )

        subject.playNextPlanarCard(testDeck)

        verify(mockDeckRepository).save(deckCaptor.capture())
        val actual = deckCaptor.value

        assertThat(actual.spatialMergingCard!!.name).isEqualTo("Plane1")
        assertThat(actual.currentCard.name).isEqualTo("Plane2")
        assertThat(actual.cards).extracting("name").containsExactly(
            "Plane3",
            "Phenomenon1",
            "Phenomenon2",
            "Spatial Merging"
        )
    }

    @Test
    fun playNextPlanarCard_spatialMerging_putsBothPlanesOnBottomOfDeck() {
        val testCurrentPlane = PlanarCard(
            "CurrentPlane",
            "plane",
            "https://api.scryfall.com",
            99999
        )
        val testSpatialMergingPlane = PlanarCard(
            "SpatialMergingPlane",
            "plane",
            "https://api.scryfall.com",
            88888
        )
        val testDeck = PlanarDeck(
            cards = testPlanes.toMutableList(),
            startTime = Date(),
            currentCard = testCurrentPlane,
            spatialMergingCard = testSpatialMergingPlane,
            id = "TEST"
        )

        subject.playNextPlanarCard(testDeck)

        verify(mockDeckRepository).save(deckCaptor.capture())
        val actual = deckCaptor.value

        assertThat(actual.cards.subList(actual.cards.size - 2, actual.cards.size)).containsExactlyInAnyOrder(
            testCurrentPlane,
            testSpatialMergingPlane
        )
    }

    @Test
    fun playNextPlanarCard_interplanarTunnel_populatesFirstFivePlanesToChooseFrom() {
        val interplanarTunnel = PlanarCard(
            "Interplanar Tunnel",
            "phenomenon",
            "https://scryfall.com/card/opca/2/interplanar-tunnel",
            423583
        )
        val testDeck = PlanarDeck(
            cards = mutableListOf(
                PlanarCard("Plane1", "plane", "https://api.scryfall.com", 12341),
                PlanarCard("Plane2", "plane", "https://api.scryfall.com", 12342),
                PlanarCard("Phenomenon1", "phenomenon", "https://api.scryfall.com", 99991),
                PlanarCard("Plane3", "plane", "https://api.scryfall.com", 12343),
                PlanarCard("Phenomenon2", "phenomenon", "https://api.scryfall.com", 99992),
                PlanarCard("Phenomenon3", "phenomenon", "https://api.scryfall.com", 99993),
                PlanarCard("Plane4", "plane", "https://api.scryfall.com", 12344),
                PlanarCard("Plane5", "plane", "https://api.scryfall.com", 12345),
                PlanarCard("Phenomenon4", "phenomenon", "https://api.scryfall.com", 99994),
                PlanarCard("Plane6", "plane", "https://api.scryfall.com", 12346),
                PlanarCard("Phenomenon5", "phenomenon", "https://api.scryfall.com", 99995)
            ),
            startTime = Date(),
            currentCard = interplanarTunnel,
            id = "TEST"
        )

        subject.playNextPlanarCard(testDeck)

        verify(mockDeckRepository).save(deckCaptor.capture())
        val actual = deckCaptor.value

        assertThat(actual.currentCard).isEqualTo(interplanarTunnel)
        assertThat(actual.interplanarCards).filteredOn("type", "plane").hasSize(5)
        assertThat(actual.interplanarCards).extracting("name").doesNotContain(
            "Phenomenon4",
            "Plane6",
            "Phenomenon5"
        )
    }

    @Test
    fun playNextPlanarCard_interplanarTunnel_setsCurrentPlaneToChosenCard() {
        val interplanarTunnel = PlanarCard(
            "Interplanar Tunnel",
            "phenomenon",
            "https://scryfall.com/card/opca/2/interplanar-tunnel",
            423583
        )
        val testDeck = PlanarDeck(
            cards = testPlanes.toMutableList(),
            startTime = Date(),
            currentCard = interplanarTunnel,
            interplanarCards = testPlanes.subList(0, 5).toMutableList(),
            id = "TEST"
        )

        whenever(mockCardRepository.findById(testPlanes[2].multiverseId!!)).thenReturn(Optional.of(testPlanes[2]))

        subject.playNextPlanarCard(testDeck, testPlanes[2].multiverseId)

        verify(mockDeckRepository).save(deckCaptor.capture())
        val actual = deckCaptor.value

        assertThat(actual.currentCard).isEqualTo(testPlanes[2])
    }

    @Test
    fun playNextPlanarCard_interplanarTunnel_putsUnchosenCardsOnBottomOfDeckRandomly() {
        val interplanarTunnel = PlanarCard(
            "Interplanar Tunnel",
            "phenomenon",
            "https://scryfall.com/card/opca/2/interplanar-tunnel",
            423583
        )
        val testDeck = PlanarDeck(
            cards = testPlanes.subList(6, 10).toMutableList(),
            startTime = Date(),
            currentCard = interplanarTunnel,
            interplanarCards = testPlanes.subList(0, 5).toMutableList(),
            id = "TEST"
        )

        whenever(mockCardRepository.findById(testPlanes[2].multiverseId!!)).thenReturn(Optional.of(testPlanes[2]))

        subject.playNextPlanarCard(testDeck, testPlanes[2].multiverseId)

        verify(mockDeckRepository).save(deckCaptor.capture())
        val actual = deckCaptor.value

        assertThat(actual.cards.last()).isEqualTo(interplanarTunnel)
        assertThat(actual.cards.subList(actual.cards.size - 6, actual.cards.size - 1))
            .containsExactlyInAnyOrderElementsOf(testPlanes.subList(0, 5))
    }

    @Test
    fun pruneOldPlanarDecks_removeAllDecksOlderThan24Hours() {
        val testDecks = listOf(
            PlanarDeck(cards = mutableListOf(), startTime = Date(), currentCard = testPlanes[0], id = "abcdef"),
            PlanarDeck(
                cards = mutableListOf(),
                startTime = DateUtils.addHours(Date(), -4),
                currentCard = testPlanes[0],
                id = "ghijkl"
            ),
            PlanarDeck(
                cards = mutableListOf(),
                startTime = DateUtils.addHours(Date(), -50),
                currentCard = testPlanes[0],
                id = "mnopqr"
            ),
            PlanarDeck(
                cards = mutableListOf(),
                startTime = DateUtils.addDays(Date(), -10),
                currentCard = testPlanes[0],
                id = "stuvwx"
            ),
            PlanarDeck(
                cards = mutableListOf(),
                startTime = DateUtils.addMonths(Date(), -1),
                currentCard = testPlanes[0],
                id = "yabbaz"
            )
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

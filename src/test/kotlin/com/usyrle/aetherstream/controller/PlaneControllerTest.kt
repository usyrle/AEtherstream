package com.usyrle.aetherstream.controller

import com.nhaarman.mockitokotlin2.verify
import com.usyrle.aetherstream.repo.PlanarDeckRepository
import com.usyrle.aetherstream.service.PlaneService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

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
    }

    @Test
    fun generateNewPlaneDeck_generatesWithRequestedValues() {
        subject.generateNewPlanarDeck(GenerateRequest(20, false))

        verify(mockPlaneService).generatePlanarDeck(20, false)
    }

    @Test
    fun generateNewPlaneDeck_defaultsToTenCardsAndUsePhenomena() {
        subject.generateNewPlanarDeck(GenerateRequest(null, null))

        verify(mockPlaneService).generatePlanarDeck(10, true)
    }
}

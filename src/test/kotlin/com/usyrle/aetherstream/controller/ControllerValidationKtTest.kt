package com.usyrle.aetherstream.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ControllerValidationKtTest {

    @Test
    fun decideDeckSize_sizeUnder80_wantPhenomena_returnRequestedSize() {
        val actual = decideDeckSize(24, true)
        assertThat(actual).isEqualTo(24)
    }

    @Test
    fun decideDeckSize_sizeOver80_wantPhenomena_return80() {
        val actual = decideDeckSize(81, true)
        assertThat(actual).isEqualTo(80)
    }

    @Test
    fun decideDeckSize_sizeUnder78_noPhenomena_returnRequestedSize() {
        val actual = decideDeckSize(74, false)
        assertThat(actual).isEqualTo(74)
    }

    @Test
    fun decideDeckSize_sizeOver78_noPhenomena_return78() {
        val actual = decideDeckSize(79, false)
        assertThat(actual).isEqualTo(78)
    }
}

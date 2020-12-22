package com.usyrle.aetherstream.repo

import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.any
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.persister.entity.EntityPersister
import org.hibernate.query.spi.QueryImplementor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import java.util.regex.Pattern

internal class DeckIdGeneratorTest {

    @Mock
    lateinit var mockSession: SharedSessionContractImplementor
    @Mock
    lateinit var mockEntityPersister: EntityPersister
    @Mock
    lateinit var mockQuery: QueryImplementor<Any>

    @BeforeEach
    fun setup() {
        initMocks(this)

        whenever(mockSession.createQuery(anyString())).thenReturn(mockQuery)
        whenever(mockSession.getEntityPersister(any(), any())).thenReturn(mockEntityPersister)
        whenever(mockEntityPersister.identifierPropertyName).thenReturn("test")
        whenever(mockQuery.resultList).thenReturn(listOf())
    }

    @Test
    fun generate_createsEightLengthAlphanumericString() {
        val subject = DeckIdGenerator()

        val actual: String = subject.generate(mockSession, "test").toString()

        assertThat(actual.length).isEqualTo(8)
        assertThat(actual).doesNotContainPattern(Pattern.compile("[^A-z0-9]"))
    }

    @Test
    fun generate_alwaysGeneratesUniqueId() {
        val subject = DeckIdGenerator()

        val actual = mutableListOf<String>()

        for (i in 1..1000) {
            actual.add(subject.generate(mockSession, "test").toString())
        }

        assertThat(actual).hasSameSizeAs(actual.toSet())
    }
}

package com.usyrle.aetherstream.repo

import org.springframework.data.repository.CrudRepository

interface PlanarCardRepository : CrudRepository<PlanarCard, Long> {
    fun findAllByType(type: String): Iterable<PlanarCard>
}

interface PlanarDeckRepository : CrudRepository<PlanarDeck, Long>

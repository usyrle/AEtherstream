package com.usyrle.aetherstream.repo

import org.springframework.data.repository.CrudRepository

interface PlaneCardRepository : CrudRepository<PlaneCard, Long> {
    fun findAllByType(type: String): Iterable<PlaneCard>
}

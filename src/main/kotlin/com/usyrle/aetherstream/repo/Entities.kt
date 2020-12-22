package com.usyrle.aetherstream.repo

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
data class PlanarCard(
    var name: String,
    var type: String,
    var scryfallUri: String,
    @Id @GeneratedValue var multiverseId: Long? = null
)

@Entity
data class PlanarDeck(
    @ManyToMany var cards: MutableList<PlanarCard>,
    @Id @GeneratedValue var id: UUID? = null
)

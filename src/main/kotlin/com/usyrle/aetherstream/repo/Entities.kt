package com.usyrle.aetherstream.repo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class PlaneCard(
        var name: String,
        var type: String,
        var scryfallUri: String,
        @Id @GeneratedValue var multiverseId: Long? = null)

class PlanarDeck(
        @OneToMany var cards: List<PlaneCard>,
        @Id @GeneratedValue var id: Long? = null)

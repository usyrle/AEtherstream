package com.usyrle.aetherstream.repo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
class PlaneCard(
        var name: String,
        var type: String,
        var scryfallUri: String,
        @Id @GeneratedValue var multiverseId: Long? = null)

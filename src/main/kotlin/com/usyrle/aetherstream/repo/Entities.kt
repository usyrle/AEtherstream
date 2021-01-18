package com.usyrle.aetherstream.repo

import org.apache.commons.lang3.RandomStringUtils
import org.hibernate.HibernateException
import org.hibernate.annotations.GenericGenerator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable
import java.util.*
import javax.persistence.*

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
    var startTime: Date = Date(),
    @OneToOne var currentCard: PlanarCard,
    @OneToOne var spatialMergingCard: PlanarCard? = null,
    @Id
    @GenericGenerator(name = "deck_id", strategy = "com.usyrle.aetherstream.repo.DeckIdGenerator")
    @GeneratedValue(generator = "deck_id")
    var id: String? = null
)

class DeckIdGenerator : IdentifierGenerator {
    @Throws(HibernateException::class)
    override fun generate(
        session: SharedSessionContractImplementor, obj: Any
    ): Serializable {
        // query taken from https://www.baeldung.com/hibernate-identifiers
        val query = String.format(
            "select %s from %s",
            session.getEntityPersister(obj.javaClass.name, obj).identifierPropertyName,
            obj.javaClass.simpleName
        )

        val ids = session.createQuery(query).resultList

        return generateId(ids)
    }

    fun generateId(currentIds: List<Any>): Serializable {
        val newId: String = RandomStringUtils.randomAlphanumeric(8)

        return if (currentIds.contains(newId)) generateId(currentIds) else newId
    }
}

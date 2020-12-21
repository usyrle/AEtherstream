package com.usyrle.aetherstream.service

import com.usyrle.aetherstream.repo.PlaneCard
import com.usyrle.aetherstream.repo.PlaneCardRepository
import com.usyrle.aetherstream.repo.PlaneSet
import org.springframework.stereotype.Service

const val PLANE_TYPE = "plane"
const val PHENOM_TYPE = "phenomenon"

@Service
class PlaneService(private val repository: PlaneCardRepository) {

    fun generatePlaneSet(): PlaneSet {
        val planes = repository.findAllByType(PLANE_TYPE).shuffled()
        val phenomena = repository.findAllByType(PHENOM_TYPE).shuffled()

        val generatedPlaneSet = (planes.subList(0, 7) + phenomena.subList(0, 2))
                .shuffled()
                .toMutableList()

        generatedPlaneSet.add(0, planes.last())

        return PlaneSet(generatedPlaneSet.toList(), null)
    }
}

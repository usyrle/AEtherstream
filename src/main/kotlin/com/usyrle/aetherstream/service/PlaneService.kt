package com.usyrle.aetherstream.service

import com.usyrle.aetherstream.repo.PlaneCardRepository
import com.usyrle.aetherstream.repo.PlaneSet
import org.springframework.stereotype.Service

const val PLANE_TYPE = "plane"
const val PHENOM_TYPE = "phenomenon"

@Service
class PlaneService(private val repository: PlaneCardRepository) {

    fun generatePlaneSet(): PlaneSet {
        val planes = repository.findAllByType(PLANE_TYPE).shuffled()

        val planeSet = PlaneSet(planes.subList(0, 10), null)

        return planeSet
    }
}

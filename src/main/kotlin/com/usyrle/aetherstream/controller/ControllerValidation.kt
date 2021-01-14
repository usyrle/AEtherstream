package com.usyrle.aetherstream.controller

const val maxPlanes = 78
const val maxPlanesAndPhenom = 80

fun decideDeckSize(requestSize: Int, usePhenomena: Boolean): Int {
    if (usePhenomena && requestSize > maxPlanesAndPhenom) {
        return maxPlanesAndPhenom
    } else if (!usePhenomena && requestSize > maxPlanes) {
        return maxPlanes
    }
    return requestSize
}

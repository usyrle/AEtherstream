package com.usyrle.aetherstream.controller

const val MAX_PLANES = 78
const val MAX_PLANES_AND_PHENOM = 80

fun decideDeckSize(requestSize: Int, usePhenomena: Boolean): Int {
    if (usePhenomena && requestSize > MAX_PLANES_AND_PHENOM) {
        return MAX_PLANES_AND_PHENOM
    } else if (!usePhenomena && requestSize > MAX_PLANES) {
        return MAX_PLANES
    }
    return requestSize
}

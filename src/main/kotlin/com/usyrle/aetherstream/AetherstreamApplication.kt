package com.usyrle.aetherstream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AetherstreamApplication

fun main(args: Array<String>) {
    runApplication<AetherstreamApplication>(*args)
}

@file:Suppress("SpringFacetCodeInspection")

package com.github.checketts.micrometerspringone2019

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.concurrent.ScheduledExecutorService
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler


@SpringBootApplication
class MicrometerSpringone2019Application {

    @Bean
    fun threadPoolTaskScheduler() = ThreadPoolTaskScheduler().apply {
        poolSize = 5
        setThreadNamePrefix("ThreadPoolTaskScheduler")
    }
}

fun main(args: Array<String>) {
    runApplication<MicrometerSpringone2019Application>(*args)
}

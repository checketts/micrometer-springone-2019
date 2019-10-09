package com.github.checketts.micrometerspringone2019

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Component
class HealthIndicatorBinder(val healthIndicators: Map<String, HealthIndicator>,
                            val executor: ThreadPoolTaskScheduler) : MeterBinder {

    val latestHealth = ConcurrentHashMap<String, Health>()

    init {
        for ((key, indicator) in healthIndicators) {
            executor.scheduleWithFixedDelay({
                latestHealth.put(key, indicator.health())
            }, Duration.ofSeconds(10))
        }
    }

    override fun bindTo(registry: MeterRegistry) {
        for ((key, value) in healthIndicators) {
            val tagKey = Tags.of("name", key)
            registry.gauge("health.indicator", tagKey, latestHealth) {
                val status = it[key]?.status ?: Status.UNKNOWN
                when (status.code) {
                    "UP" -> 1.0
                    "DOWN" -> -1.0
                    "OUT_OF_SERVICE" -> -2.0
                    "UNKNOWN" -> -3.0
                    else -> -3.0
                }
            }
        }
    }
}
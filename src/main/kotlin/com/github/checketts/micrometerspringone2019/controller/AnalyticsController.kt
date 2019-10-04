package com.github.checketts.micrometerspringone2019.controller

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentHashMap

private val logger = LoggerFactory.getLogger(AnalyticsController::class.java)

@RestController
@RequestMapping("api/analytics")
class AnalyticsController(private val meterRegistry: MeterRegistry) {

    val clientMetricsCap = 3
    val knownComponentRoutes = ConcurrentHashMap<String,String>()

    @PostMapping("metrics")
    fun recordClientMetrics(@RequestBody metrics: Map<String, Int>){
        metrics.forEach {(key, value) ->
            val filteredKey: String
            if(knownComponentRoutes.size < clientMetricsCap) {
                knownComponentRoutes[key] = key
                filteredKey = key
            } else {
                if(knownComponentRoutes.contains(key)) {
                    filteredKey = key
                } else {
                    logger.warn("Too many unique client.visit values grouping {} as 'other'", key)
                    filteredKey = "other"
                }
            }

            meterRegistry.counter("client.visits",
                Tags.of("component", filteredKey)).increment(value.toDouble())}
    }

}
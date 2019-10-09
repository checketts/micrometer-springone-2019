package com.github.checketts.micrometerspringone2019

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
import io.micrometer.core.instrument.logging.LoggingRegistryConfig
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import java.time.Duration

private val Int.minutes: Duration get() = Duration.ofMinutes(this.toLong())

private data class Chore0(val name: String, val duration: Duration, val group: String = "home")


private val chores0 = listOf(
        Chore0("Mow the front lawn", 25.minutes, "lawn"),
        Chore0("Mow the back lawn", 15.minutes, "lawn"),
        Chore0("Gather laundry", 5.minutes, "laundry"),
        Chore0("Wash the laundry", 5.minutes, "laundry"),
        Chore0("Sort and fold the laundry", 35.minutes, "laundry"),
        Chore0("Wash dishes", 15.minutes, "kitchen"),
        Chore0("Find my phone charger", Duration.ofNanos(5))
)

fun main() {
    val meterRegistry = Metrics.globalRegistry
    val simpleRegistry = SimpleMeterRegistry().apply { meterRegistry.add(this) }
    val config = object: LoggingRegistryConfig {
        override fun get(key: String)=null
        override fun logInactive()= true
        override fun step()= Duration.ofSeconds(10)
    }
    val loggingRegistry = LoggingMeterRegistry(config, Clock.SYSTEM).apply {
        meterRegistry.add(this) }
    loggingRegistry.config().meterFilter(MeterFilter.accept { it.name == "chore.completed" })
    meterRegistry.config().meterFilter(MeterFilter.commonTags(Tags.of("team", "spring")))

    val groupChores = object : MeterFilter {
        override fun map(id: Meter.Id): Meter.Id {
            if(id.name == "chore.duration") {
                return id.replaceTags(id.tags.map { if(it.key == "group" && it.value == "laundry") it else Tag.of(it.key,"other") })
            } else {
                return id
            }
        }
    }
    meterRegistry.config().meterFilter(groupChores)
//    meterRegistry.config().meterFilter(MeterFilter.maximumAllowableMetrics(3))

    addGauge0(meterRegistry)


    for (chore in chores0) {
        println("Doing my chores: $chore")
        meterRegistry.counter("chore.completed").increment()
        meterRegistry.timer("chore.duration", Tags.of("group", chore.group)).record(chore.duration)
    }

    for (meter in simpleRegistry.meters) {
        println("${meter.id} ${meter.measure()}")
    }

    System.gc()
    (1..100).forEach { Thread.sleep(1000); println("Sleep $it") }
}

private fun addGauge0(meterRegistry: CompositeMeterRegistry) {
    val list = chores0.map { it }
    Gauge.builder("chore.store.strong", list, { list.size.toDouble()}).strongReference(true).register(meterRegistry)
    meterRegistry.gauge("chore.store.lambda", list, { list.size.toDouble()})
    meterRegistry.gauge("chore.store.weak", list, { it.size.toDouble()})
}

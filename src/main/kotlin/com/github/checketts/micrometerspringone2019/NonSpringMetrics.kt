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

private val Int.minutes get() = Duration.ofMinutes(this.toLong())
data class Chore(val name: String, val duration: Duration, val group: String = "home")

val chores = listOf(
        Chore("Mow front lawn", 20.minutes, "yard"),
        Chore("Mow back lawn", 10.minutes, "yard"),
        Chore("Gather dirty laundry", 10.minutes, "laundry"),
        Chore("Load laundry", 5.minutes, "laundry"),
        Chore("Sort laundry", 45.minutes, "laundry"),
        Chore("Wash dishes", 10.minutes, "kitchen"),
        Chore("Find my phone charger", Duration.ofNanos(5))
)

fun main() {
    val meterRegistry = Metrics.globalRegistry
    val simple = SimpleMeterRegistry().apply { meterRegistry.add(this) }

    val config = object: LoggingRegistryConfig {
        override fun get(key: String)=null
        override fun logInactive()= true
        override fun step()= Duration.ofSeconds(5)
    }
    val loggingRegistry = LoggingMeterRegistry(config, Clock.SYSTEM).apply {
        meterRegistry.add(this) }
//    loggingRegistry.config().meterFilter(MeterFilter.accept { it.name == "chore.completed" })
//    loggingRegistry.config().meterFilter(MeterFilter.deny())


//    meterRegistry.config().meterFilter(MeterFilter.deny { it.name == "chore.completed"})
//    meterRegistry.config().meterFilter(MeterFilter.maximumAllowableMetrics(3))
    val groupingMeterFilter = object: MeterFilter {
        override fun map(id: Meter.Id): Meter.Id {
            if (id.name == "chore.time") {
                return id.replaceTags(id.tags.map { if (it.value == "laundry") it else Tag.of(it.key, "other") })
            } else {
                return id
            }
        }
    }
    meterRegistry.config().meterFilter(groupingMeterFilter)

    addGauge(meterRegistry)
    for (chore in chores) {
        println("Doing my chore: ${chore.name}")
        meterRegistry.counter("chore.completed").increment()
        meterRegistry.timer("chore.time", Tags.of("group", chore.group)).record(chore.duration)
    }

    for (meter in simple.meters) {
        println("${meter.id} ${meter.measure()}")
    }

    System.gc()
    (1..100).forEach {
        Thread.sleep(1000)
        println("Waiting $it")
    }
}

private fun addGauge(meterRegistry: CompositeMeterRegistry) {
    val choreList = chores.map { it }
//    meterRegistry.gauge("chore.size.weak", choreList, {it.size.toDouble()})
//    meterRegistry.gauge("chore.size.lambda", "", {choreList.size.toDouble()})
    Gauge.builder("chore.size.strong", choreList, {it.size.toDouble()}).strongReference(true).register(meterRegistry)
}

package com.github.checketts.micrometerspringone2019

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import java.time.Duration


data class DailyChores(val chores: MutableList<Chore> = mutableListOf()) {
    infix fun String.takes(duration: Duration): Chore {
        val chore = Chore(this, duration)
        chores.add(chore)
        return chore
    }

    val Int.minutes: Duration get() = Duration.ofMinutes(this.toLong())
    operator fun Chore.invoke(block: Chore.() -> Unit) {
        this.block()
    }

    operator fun String.invoke(block: Chore.() -> Unit): Chore {
        val chore = Chore(this)
        chore.block()
        chores.add(chore)
        return chore
    }
}

fun dailyChores(block: DailyChores.() -> Unit): List<Chore> {
    val dailyChores = DailyChores()
    block.invoke(dailyChores)
    return dailyChores.chores
}

data class Chore(val name: String, var duration: Duration = Duration.ZERO, var by: String? = null)








val chores = dailyChores {
    "Mow front yard" {
        duration = 30.minutes
        by = "Clint"
    }
    "Mow back yard" takes 20.minutes
    "Collect laundry" takes 3.minutes
    "Wash laundry" takes 5.minutes
    "Sort laundry" takes 15.minutes
    "Take out trash" takes 5.minutes
    "Wash dishes" takes 15.minutes
    "Make bed" takes 1.minutes
    "Find something" takes Duration.ofNanos(5)
}


fun main() {
    val meterRegistry = SimpleMeterRegistry() //Metrics.globalRegistry
    val groupChores = object : MeterFilter {
        override fun map(id: Meter.Id): Meter.Id {
            return id.replaceTags(id.tags.map {
                when {
                    it.value.toLowerCase().contains("mow") -> Tag.of(it.key, "yardwork")
                    it.value.toLowerCase().contains("laundry") -> Tag.of(it.key, "laundry")
                    else -> Tag.of(it.key, "other")
                }
            })
        }
    }
    meterRegistry.config().meterFilter(groupChores)

    chores.forEach {
        println("Doing my chore: ${it.name}")
        meterRegistry.counter("chores.done").increment()
        meterRegistry.timer("chore.timing", Tags.of("task", it.name)).record(it.duration)
    }

    meterRegistry.meters.forEach { println("${it.id} ${it.measure()}") }
}
package com.github.checketts.micrometerspringone2019.services

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.checketts.upstreamService.User
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.getForObject
import kotlin.random.Random


private val logger = LoggerFactory.getLogger(UserService::class.java)

@Service
class UserService(
        cbReg: CircuitBreakerRegistry,
        meterRegistry: MeterRegistry,
        restTemplateBuilder: RestTemplateBuilder
) {
    private val restTemplate = restTemplateBuilder.build()
    private val userCircuitBreaker = cbReg.circuitBreaker("user-lookup").apply {
        eventPublisher.onStateTransition { event -> logger.warn("CircuitBreaker: $event") }
    }

    val userCache = CaffeineCacheMetrics.monitor(meterRegistry,Caffeine.newBuilder()
            .maximumSize(1).build<String, List<User>>(), "users")


    fun getUsers(): List<User> {
        return userCircuitBreaker.protect(fallback = {userCache.getIfPresent("users") ?: listOf()}) {
            val users = fetchUsers()
            userCache.put("users", users)
            users
        }
    }

    private fun fetchUsers() : List<User> {
        val shouldFail = Random.nextInt(1,5)
        return restTemplate.getForObject("http://localhost:8083/users/{shouldFail}", shouldFail) ?: throw RuntimeException("error")
    }


}
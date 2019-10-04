package com.github.checketts.micrometerspringone2019.services

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.vavr.control.Try

fun <T> CircuitBreaker.protect(fallback:(Throwable) -> T, block: () -> T): T {
  val decoratedSupplier = CircuitBreaker.decorateSupplier(this, block)
  // Execute the decorated supplier and recover from any exception
  return Try.ofSupplier(decoratedSupplier).recover(fallback).get()
}

operator fun <T> CircuitBreaker.invoke(block: () -> T): T {

  return this.executeSupplier(block)
}

class CircuitBreakerDsl<T>(private val circuitBreaker: CircuitBreaker) {

  var closure: (() -> T)? = null
  var fallback: ((Throwable) -> T)? = null

  fun decorate(): T {
    val decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, closure ?: throw RuntimeException("Executed block is required"))
    // Execute the decorated supplier and recover from any exception
    return Try.ofSupplier(decoratedSupplier).recover(fallback).get()
  }
}

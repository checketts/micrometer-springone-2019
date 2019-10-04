package com.github.checketts.micrometerspringone2019.controller

import com.github.checketts.micrometerspringone2019.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
@RequestMapping("api/hello")
class HelloWorldController(
        val userService: UserService
) {

    @GetMapping
    fun hello(): String {
        userService.getUsers()
        val wait = Random.nextLong(1000,5000)
        Thread.sleep(wait)
        return "Hello (after waiting $wait ms)"
    }

    @GetMapping("quick")
    fun helloQuick(): String {
        userService.getUsers()
        return "Hello (after no waiting)"
    }
}
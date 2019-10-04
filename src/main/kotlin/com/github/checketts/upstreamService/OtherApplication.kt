package com.github.checketts.upstreamService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.lang.RuntimeException

data class User(val id: Long, val name: String)

@SpringBootApplication
@RestController
class OtherApplication{

	@GetMapping("users/{shouldFail}")
	fun users(@PathVariable shouldFail: Int): List<User> {
		if(shouldFail == 1) {
			throw RuntimeException("Boom! Taking a break,")
		}

		return listOf(
				User(1, "Clint")
		)
	}

}

fun main(args: Array<String>) {
	runApplication<OtherApplication>(*args) {
		setDefaultProperties(mapOf("server.port" to "8083"))
	}
}

package com.joney.coupon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class JoneyApplication

fun main(args: Array<String>) {
    runApplication<JoneyApplication>(*args)
}

package com.joney.coupon.infrastructure.cache

import org.aspectj.apache.bcel.util.ClassPath
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript

fun listLuaScript(path: String): RedisScript<List<*>> =
    RedisScript.of(ClassPathResource(path), List::class.java)

fun longLuaScript(path: String): RedisScript<Long> =
    RedisScript.of(ClassPathResource(path), Long::class.java)

@Suppress("UNCHECKED_CAST")
fun StringRedisTemplate.runForString(
    script: RedisScript<List<*>>,
    keys: List<String>,
    vararg args: Any,
): List<String> = execute(script, keys, *args.map { it.toString() }.toTypedArray()) as List<String>

fun StringRedisTemplate.runForLong(
    script: RedisScript<Long>,
    keys: List<String>,
    vararg args: Any,
): Long = execute(script, keys, *args.map { it.toString() }.toTypedArray()) ?: error("Lua결과가 null 입니다.")
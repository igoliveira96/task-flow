package br.com.goulart.taskflow.domain.time

import org.koin.core.annotation.Single

fun interface TimeProvider {
    fun currentTimeMillis(): Long
}

@Single
class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

package br.com.goulart.taskflow.domain.task

import java.util.UUID
import org.koin.core.annotation.Single

fun interface TaskCodeGenerator {
    fun nextCode(): String
}

@Single
class UuidTaskCodeGenerator : TaskCodeGenerator {
    override fun nextCode(): String =
        "TF-${UUID.randomUUID().toString().take(CODE_SUFFIX_LENGTH).uppercase()}"

    private companion object {
        const val CODE_SUFFIX_LENGTH = 8
    }
}

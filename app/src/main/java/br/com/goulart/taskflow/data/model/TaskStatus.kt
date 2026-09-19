package br.com.goulart.taskflow.data.model

enum class TaskStatus(val storageValue: String) {
    TODO("TODO"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE"),
    ;

    companion object {
        fun fromStorageValue(value: String): TaskStatus =
            requireNotNull(entries.firstOrNull { it.storageValue == value }) {
                "Unknown task status: $value"
            }
    }
}

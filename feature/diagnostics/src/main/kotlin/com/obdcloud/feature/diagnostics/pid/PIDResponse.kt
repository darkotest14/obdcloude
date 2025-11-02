package com.obdcloud.feature.diagnostics.pid

import java.time.Instant

/**
 * Represents a response from a PID request with calculated value and metadata
 */
data class PIDResponse(
    val mode: Int,
    val pid: Int,
    val rawData: ByteArray,
    val timestamp: Instant = Instant.now(),
    val calculatedValue: Number,
    val unit: String,
    val name: String,
    val description: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PIDResponse) return false

        if (mode != other.mode) return false
        if (pid != other.pid) return false
        if (!rawData.contentEquals(other.rawData)) return false
        if (timestamp != other.timestamp) return false
        if (calculatedValue != other.calculatedValue) return false
        if (unit != other.unit) return false
        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mode
        result = 31 * result + pid
        result = 31 * result + rawData.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + calculatedValue.hashCode()
        result = 31 * result + unit.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        return result
    }
}

/**
 * Factory for creating PIDResponse objects from raw data
 */
object PIDResponseFactory {
    fun createResponse(mode: Int, pid: Int, data: ByteArray): PIDResponse? {
        val definition = when(mode) {
            0x01 -> Mode01PIDs.getPIDDefinition(pid)
            // Add more modes as needed
            else -> null
        } ?: return null

        return PIDResponse(
            mode = mode,
            pid = pid,
            rawData = data,
            calculatedValue = definition.formula(data),
            unit = definition.unit,
            name = definition.name,
            description = definition.description
        )
    }
}
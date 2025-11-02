package com.obdcloud.feature.diagnostics.pid

object PIDFormulas {
    fun calculateEngineLoad(data: ByteArray): Float {
        return (data[0].toInt() and 0xFF) * 100f / 255f
    }
    
    fun calculateEngineCoolantTemp(data: ByteArray): Int {
        return (data[0].toInt() and 0xFF) - 40
    }
    
    fun calculateFuelTrim(data: ByteArray): Float {
        return ((data[0].toInt() and 0xFF) - 128) * 100f / 128f
    }
    
    fun calculateFuelPressure(data: ByteArray): Int {
        return (data[0].toInt() and 0xFF) * 3
    }
    
    fun calculateIntakeManifoldPressure(data: ByteArray): Int {
        return data[0].toInt() and 0xFF
    }
    
    fun calculateEngineRPM(data: ByteArray): Int {
        return ((data[0].toInt() and 0xFF) * 256 + (data[1].toInt() and 0xFF)) / 4
    }
    
    fun calculateVehicleSpeed(data: ByteArray): Int {
        return data[0].toInt() and 0xFF
    }
    
    fun calculateTimingAdvance(data: ByteArray): Float {
        return (data[0].toInt() and 0xFF) / 2f - 64f
    }
    
    fun calculateIntakeAirTemp(data: ByteArray): Int {
        return (data[0].toInt() and 0xFF) - 40
    }
    
    fun calculateMAF(data: ByteArray): Float {
        return ((data[0].toInt() and 0xFF) * 256 + (data[1].toInt() and 0xFF)) / 100f
    }
    
    fun calculateThrottlePosition(data: ByteArray): Float {
        return (data[0].toInt() and 0xFF) * 100f / 255f
    }
    
    fun calculateOxygenSensorVoltage(data: ByteArray): Float {
        return (data[0].toInt() and 0xFF) / 200f
    }
    
    fun calculateOxygenSensorFuelTrim(data: ByteArray): Float {
        return ((data[1].toInt() and 0xFF) - 128) * 100f / 128f
    }
}
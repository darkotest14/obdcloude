package com.obdcloud.feature.diagnostics.pid

/**
 * Standard OBD-II PIDs (Parameter IDs) as defined in SAE J1979
 */
object StandardPIDs {
    // Mode 01 - Show current data
    const val SUPPORTED_PIDS_1_20 = 0x00
    const val MONITOR_STATUS = 0x01
    const val FREEZE_DTC = 0x02
    const val FUEL_SYSTEM_STATUS = 0x03
    const val CALCULATED_ENGINE_LOAD = 0x04
    const val ENGINE_COOLANT_TEMP = 0x05
    const val SHORT_TERM_FUEL_TRIM_1 = 0x06
    const val LONG_TERM_FUEL_TRIM_1 = 0x07
    const val SHORT_TERM_FUEL_TRIM_2 = 0x08
    const val LONG_TERM_FUEL_TRIM_2 = 0x09
    const val FUEL_PRESSURE = 0x0A
    const val INTAKE_MANIFOLD_PRESSURE = 0x0B
    const val ENGINE_RPM = 0x0C
    const val VEHICLE_SPEED = 0x0D
    const val TIMING_ADVANCE = 0x0E
    const val INTAKE_AIR_TEMP = 0x0F
    const val MAF_SENSOR = 0x10
    const val THROTTLE_POSITION = 0x11
    const val COMMANDED_SECONDARY_AIR_STATUS = 0x12
    const val OXYGEN_SENSORS_PRESENT = 0x13
    const val OXYGEN_SENSOR_1 = 0x14
    const val OXYGEN_SENSOR_2 = 0x15
    const val OXYGEN_SENSOR_3 = 0x16
    const val OXYGEN_SENSOR_4 = 0x17
    const val OXYGEN_SENSOR_5 = 0x18
    const val OXYGEN_SENSOR_6 = 0x19
    const val OXYGEN_SENSOR_7 = 0x1A
    const val OXYGEN_SENSOR_8 = 0x1B
    const val OBD_STANDARDS = 0x1C
    const val SUPPORTED_PIDS_21_40 = 0x20
    
    // Mode 02 - Show freeze frame data
    // Uses same PIDs as Mode 01
    
    // Mode 03 - Show stored DTCs
    // No PIDs, just request Mode 03
    
    // Mode 04 - Clear DTCs and stored values
    // No PIDs, just request Mode 04
    
    // Mode 05 - Test results, oxygen sensors
    // Manufacturer specific
    
    // Mode 06 - Test results, other components
    // Manufacturer specific
    
    // Mode 07 - Show pending DTCs
    // No PIDs, just request Mode 07
    
    // Mode 08 - Control operations
    // Manufacturer specific
    
    // Mode 09 - Vehicle information
    const val VIN_MESSAGE_COUNT = 0x01
    const val VIN = 0x02
    const val CALIBRATION_ID_MESSAGE_COUNT = 0x03
    const val CALIBRATION_ID = 0x04
    const val CVN_MESSAGE_COUNT = 0x05
    const val CVN = 0x06
    const val IN_USE_PERFORMANCE_TRACKING = 0x07
    const val ECU_NAME_MESSAGE_COUNT = 0x08
    const val ECU_NAME = 0x09
    
    // Mode 0A - Permanent DTCs
    // No PIDs, just request Mode 0A
}
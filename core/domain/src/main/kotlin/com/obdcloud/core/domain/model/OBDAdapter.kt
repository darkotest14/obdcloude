package com.obdcloud.core.domain.model

/**
 * Represents an OBD adapter device that can be used for vehicle diagnostics
 */
data class OBDAdapter(
    val id: String,
    val name: String,
    val macAddress: String?,
    val ipAddress: String?,
    val port: Int?,
    val connectionType: ConnectionType,
    val status: ConnectionStatus,
    val supportedProtocols: List<Protocol>
)

enum class ConnectionType {
    BLUETOOTH_LE,
    BLUETOOTH_SPP,
    WIFI_TCP,
    USB
}

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

enum class Protocol {
    ISO_15765_4_CAN,      // CAN 11/29 bit, 250/500 kbps
    ISO_14230_4_KWP,      // Keyword Protocol 2000
    ISO_9141_2,           // K-line
    SAE_J1850_PWM,        // PWM (41.6 kbps)
    SAE_J1850_VPW,        // VPW (10.4 kbps)
    ISO_15765_4_CAN_FD    // CAN-FD support
}
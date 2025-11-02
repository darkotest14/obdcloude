package com.obdcloud.feature.diagnostics.protocol

import com.obdcloud.core.domain.model.Protocol
import javax.inject.Inject

class ProtocolFactory @Inject constructor() {
    fun createProtocol(
        protocol: Protocol,
        sendCommand: suspend (ByteArray) -> Result<ByteArray>
    ): OBDProtocol {
        return when (protocol) {
            Protocol.ISO_15765_4_CAN -> ISO15765Protocol(sendCommand)
            Protocol.ISO_14230_4_KWP -> KWP2000Protocol(sendCommand)
            Protocol.ISO_9141_2 -> ISO9141Protocol(sendCommand)
            Protocol.SAE_J1850_PWM,
            Protocol.SAE_J1850_VPW,
            Protocol.ISO_15765_4_CAN_FD -> throw UnsupportedOperationException("Protocol not implemented: $protocol")
        }
    }
    
    fun createUDSProtocol(transportProtocol: OBDProtocol): UDSProtocol {
        return UDSProtocol(transportProtocol)
    }
}
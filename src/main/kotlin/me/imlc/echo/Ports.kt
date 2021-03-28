package me.imlc.echo

import org.slf4j.LoggerFactory
import java.net.ServerSocket

private val logger = LoggerFactory.getLogger("me.imlc.echo.Ports")

fun freePort(): Int {
    ServerSocket(0).use { serverSocket ->
        return serverSocket.localPort
    }
}
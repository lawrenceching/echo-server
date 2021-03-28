package me.imlc.echo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import okio.ByteString.Companion.readByteString
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.net.InetSocketAddress

fun HttpExchange.getBodyAsString(): String? {

    fun getBody(): String {
        return this.requestBody.bufferedReader().use { it.readText() }
    }

    return when (this.requestMethod) {
        "GET" -> null
        "HEAD" -> null
        "POST" -> getBody()
        "PUT" -> getBody()
        "PATCH" -> getBody()
        else -> null
    }
}

class App {

    private val logger = LoggerFactory.getLogger(App::class.java)
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private lateinit var server: HttpServer

    fun start(port: Int) {
        server = HttpServer.create(
            InetSocketAddress(port), 0
        )

        server.createContext("/", object : HttpHandler {
            override fun handle(exchange: HttpExchange) {
                val text = gson.toJson(
                    Response(
                        method = exchange.requestMethod,
                        hostname = exchange.localAddress.let { "${it.hostName}:${it.port}" },
                        uri = exchange.requestURI.toASCIIString(),
                        body = exchange.getBodyAsString()
                    )
                )
                exchange.sendResponseHeaders(200, text.length.toLong())
                val os = exchange.responseBody
                os.write(text.toByteArray())
                os.close()
            }
        })

        server.setExecutor(null)
        server.start()
        logger.info("Echo started at localhost:$port")
    }

    fun stop() {
        server.stop(10)
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val app = App()
            app.start(8080)
        }
    }

    data class Response(
        private val method: String,
        private val hostname: String,
        private val uri: String,
        private val body: String?,
    )
}
package me.imlc.echo

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class AppTest {

    private val logger = LoggerFactory.getLogger(AppTest::class.java)
    private val client = OkHttpClient.Builder().build()
    private lateinit var app: App
    private var port: Int = 8080

    @BeforeEach
    internal fun setUp() {
        port = freePort()
        logger.info("Started Echo server at localhost:$port")
        app = App()
        app.start(port)
    }

    @AfterEach
    internal fun tearDown() {
        app.stop()
    }

    @Test
    internal fun canEchoHttpGet() {
        val req = Request.Builder()
            .url("http://localhost:$port")
            .build()

        val resp = client.newCall(req)
            .execute()

        assertEquals(200, resp.code)
        //language: JSON
        assertEquals(
            """
            {
              "method": "GET",
              "hostname": "localhost:$port",
              "uri": "/"
            }
        """.trimIndent(), resp.body?.string()
        )
    }

    @Test
    internal fun canEchoHttpGet2() {
        val req = Request.Builder()
            .url("http://localhost:$port/api/v1/books")
            .build()

        val resp = client.newCall(req)
            .execute()

        assertEquals(200, resp.code)
        //language: JSON
        assertEquals(
            """
            {
              "method": "GET",
              "hostname": "localhost:$port",
              "uri": "/api/v1/books"
            }
        """.trimIndent(), resp.body?.string()
        )
    }

    @Test
    internal fun canEchoHttpPost() {
        val req = Request.Builder()
            .url("http://localhost:$port/api/v1/books")
            .method(
                "POST",
                "key: value".toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
            )
            .build()

        val resp = client.newCall(req)
            .execute()

        assertEquals(200, resp.code)
        //language: JSON
        assertEquals(
            """
            {
              "method": "POST",
              "hostname": "localhost:$port",
              "uri": "/api/v1/books",
              "body": "key: value"
            }
        """.trimIndent(), resp.body?.string()
        )
    }
}
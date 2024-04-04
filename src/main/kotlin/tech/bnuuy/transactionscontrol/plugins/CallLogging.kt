package tech.bnuuy.transactionscontrol.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.request.*

fun Application.configureCallLogging() {
    install(ForwardedHeaders)
    install(CallLogging) {
        filter { call -> call.request.path() !in arrayOf("/favicon.ico") }
        format { call ->
            val origin = call.request.origin.remoteHost
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.userAgent()
            val uri = call.request.uri.replace(Regex("\\?code=.*"), "\\?code=[REDACTED]")
            val time = call.processingTimeMillis()
            "from $origin ($userAgent): $httpMethod $uri, got $status in ${time}ms"
        }
    }
}
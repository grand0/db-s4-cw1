package tech.bnuuy.transactionscontrol

import io.ktor.server.application.*
import tech.bnuuy.transactionscontrol.db.DatabaseSingleton
import tech.bnuuy.transactionscontrol.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseSingleton.init()

    configureCallLogging()
    configureTemplates()
    configureContentNegotiation()
    configureRouting()
}

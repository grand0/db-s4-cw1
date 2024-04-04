package tech.bnuuy.transactionscontrol.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.jetbrains.exposed.sql.transactions.transaction
import tech.bnuuy.transactionscontrol.db.TransactionsSummary
import tech.bnuuy.transactionscontrol.repository.TransactionsRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

private const val PAGE_ITEMS_LIMIT = 50
private val DATE_FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d")
private val DATE_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M")

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")

        get("/") {
            val currentPage = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val transactions = TransactionsRepository.getAll(
                limit = PAGE_ITEMS_LIMIT,
                page = currentPage,
            );
            val summary = TransactionsRepository.summary();
            call.respond(ThymeleafContent(
                "index",
                mapOf(
                    "transactions" to transactions,
                    "totalAmount" to summary.totalAmount,
                    "totalTransactions" to summary.transactionCount,
                    "totalPages" to ceil(summary.transactionCount / PAGE_ITEMS_LIMIT.toDouble()),
                    "currentPage" to currentPage,
                    "offset" to ((currentPage - 1) * PAGE_ITEMS_LIMIT)
                )
            ))
        }
        get("/stats") {
            call.respond(ThymeleafContent(
                "stats",
                mapOf()
            ))
        }

        get("/stats.json") {
            val summaryBy = call.request.queryParameters["by"] ?: "countries"
            val summary = when (summaryBy) {
                "countries" -> TransactionsRepository.summaryCountries()
                "currencies" -> TransactionsRepository.summaryCurrencies()
                "payment_statuses" -> TransactionsRepository.summaryPaymentStatuses()
                else -> null
            }
            if (summary != null) {
                call.respond(summary)
            } else {
                call.respondText("{\"error\": 1}", status = HttpStatusCode.BadRequest, contentType = ContentType.Application.Json)
            }
        }

        get("/stats.json/month") {
            val date = call.request.queryParameters["date"]?.let {
                LocalDate.parse(it, DATE_FULL_FORMATTER)
            } ?: LocalDate.now()
            val summaryBy = call.request.queryParameters["by"] ?: "countries"
            val summary = when (summaryBy) {
                "countries" -> TransactionsRepository.summaryCountriesMonth(date)
                "currencies" -> TransactionsRepository.summaryCurrenciesMonth(date)
                "payment_statuses" -> TransactionsRepository.summaryPaymentStatusesMonth(date)
                else -> null
            }
            if (summary != null) {
                call.respond(mapOf(
                    "monthSummary" to summary,
                    "month" to date.format(DATE_MONTH_FORMATTER),
                ))
            } else {
                call.respondText("{\"error\": 1}", status = HttpStatusCode.BadRequest, contentType = ContentType.Application.Json)
            }
        }
    }
}

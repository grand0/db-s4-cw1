package tech.bnuuy.transactionscontrol.repository

import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CustomDateFunction
import org.jetbrains.exposed.sql.transactions.transaction
import tech.bnuuy.transactionscontrol.db.*
import tech.bnuuy.transactionscontrol.db.Transaction
import java.math.BigDecimal
import java.time.LocalDate

object TransactionsRepository {

    private val transactionDateTruncMonth = CustomDateFunction(
        "date_trunc",
        stringLiteral("month"),
        Transactions.date
    )

    fun getAll(limit: Int? = 50, page: Int? = null): List<Transaction> {
        val limit = limit?.coerceIn(0, null)
        val offset = ((page?.coerceIn(1, null) ?: 1) - 1) * (limit ?: 0)
        return transaction {
            var transactions = Transaction.all()
                .orderBy(
                    Transactions.date to SortOrder.DESC,
                    Transactions.id to SortOrder.DESC,
                )
            if (limit != null) {
                transactions = transactions.limit(limit, offset.toLong())
            }
            transactions
                .toList()
                .with(
                    Transaction::paymentType,
                    Transaction::paymentMethod,
                    Transaction::processingStatus,
                    Transaction::paymentStatus,
                )
        }
    }

    fun summary(): TransactionsSummary {
        return transaction {
            val totalAmount = Transactions.amount.sum()
            val totalCount = Transactions.id.count()
            Transactions.select(
                totalAmount,
                totalCount,
            ).map {
                TransactionsSummary(
                    totalAmount = it[totalAmount] ?: BigDecimal.ZERO,
                    transactionCount = it[totalCount],
                )
            }.single()
        }
    }

    fun summaryCountries(): Map<String, TransactionsSummary> {
        return transaction {
            val countryCode = Transactions.countryCode
            val totalAmount = Transactions.amount.sum()
            val transactionsCount = Transactions.id.count()
            Transactions.select(
                countryCode,
                totalAmount,
                transactionsCount,
            )
                .groupBy(countryCode)
                .groupBy { it[countryCode] }
                .mapValues {
                    it.value.single().let {
                        TransactionsSummary(
                            totalAmount = it[totalAmount] ?: BigDecimal.ZERO,
                            transactionCount = it[transactionsCount],
                        )
                    }
                }
        }
    }

    fun summaryCountriesMonthly(): Map<LocalDate, Map<String, TransactionsSummary>> {
        return transaction {
            val month = transactionDateTruncMonth
            val countryCode = Transactions.countryCode
            val totalAmount = Transactions.amount.sum()
            val totalCount = Transactions.id.count()
            Transactions.select(
                month,
                countryCode,
                totalAmount,
                totalCount,
            )
                .groupBy(month, countryCode)
                .orderBy(
                    month to SortOrder.DESC,
                    countryCode to SortOrder.ASC,
                )
                .groupBy {
                    it[month]!!
                }
                .mapValues {
                    it.value
                        .groupBy { it[countryCode] }
                        .mapValues {
                            it.value.single().let {
                                TransactionsSummary(
                                    totalAmount = it[totalAmount] ?: BigDecimal.ZERO,
                                    transactionCount = it[totalCount],
                                )
                            }
                        }
                }
        }
    }

    fun summaryCountriesMonth(date: LocalDate): Map<String, TransactionsSummary> {
        return summaryCountriesMonthly()[date.withDayOfMonth(1)] ?: mapOf()
    }

    fun summaryCurrencies(): Map<String, TransactionsSummary> {
        return transaction {
            val currency = Transactions.currency
            val totalAmount = Transactions.amount.sum()
            val transactionsCount = Transactions.id.count()
            Transactions.select(
                currency,
                totalAmount,
                transactionsCount,
            )
                .groupBy(currency)
                .groupBy { it[currency] }
                .mapValues {
                    it.value.single().let {
                        TransactionsSummary(
                            totalAmount = it[totalAmount] ?: BigDecimal.ZERO,
                            transactionCount = it[transactionsCount],
                        )
                    }
                }
        }
    }

    fun summaryCurrenciesMonthly(): Map<LocalDate, Map<String, TransactionsSummary>> {
        return transaction {
            val month = transactionDateTruncMonth
            val currency = Transactions.currency
            val totalAmount = Transactions.amount.sum()
            val totalCount = Transactions.id.count()
            Transactions.select(
                month,
                currency,
                totalAmount,
                totalCount,
            )
                .groupBy(month, currency)
                .orderBy(
                    month to SortOrder.DESC,
                    currency to SortOrder.ASC,
                )
                .groupBy {
                    it[month]!!
                }
                .mapValues {
                    it.value
                        .groupBy { it[currency] }
                        .mapValues {
                            it.value.single().let {
                                TransactionsSummary(
                                    totalAmount = it[totalAmount] ?: BigDecimal.ZERO,
                                    transactionCount = it[totalCount],
                                )
                            }
                        }
                }
        }
    }

    fun summaryCurrenciesMonth(date: LocalDate): Map<String, TransactionsSummary> {
        return summaryCurrenciesMonthly()[date.withDayOfMonth(1)] ?: mapOf()
    }

    fun summaryPaymentStatuses(): Map<String, TransactionsSummary> {
         return transaction {
            val paymentStatus = PaymentStatuses.name
            val totalAmount = Transactions.amount.sum()
            val transactionsCount = Transactions.id.count()
             (Transactions innerJoin PaymentStatuses)
                .select(
                    paymentStatus,
                    totalAmount,
                    transactionsCount,
                )
                .groupBy(paymentStatus)
                .groupBy { it[paymentStatus] }
                .mapValues {
                    it.value.single().let {
                        TransactionsSummary(
                            totalAmount = it[totalAmount] ?: BigDecimal.ZERO,
                            transactionCount = it[transactionsCount],
                        )
                    }
                }
        }
    }

    fun summaryPaymentStatusesMonthly(): Map<LocalDate, Map<String, TransactionsSummary>> {
        return transaction {
            val month = transactionDateTruncMonth
            val paymentStatus = PaymentStatuses.name
            val totalAmount = Transactions.amount.sum()
            val totalCount = Transactions.id.count()
            (Transactions innerJoin PaymentStatuses)
                .select(
                    month,
                    paymentStatus,
                    totalAmount,
                    totalCount,
                )
                .groupBy(month, paymentStatus)
                .orderBy(
                    month to SortOrder.DESC,
                    paymentStatus to SortOrder.ASC,
                )
                .groupBy {
                    it[month]!!
                }
                .mapValues {
                    it.value
                        .groupBy { it[paymentStatus] }
                        .mapValues {
                            it.value.single().let {
                                TransactionsSummary(
                                    totalAmount = it[totalAmount] ?: BigDecimal.ZERO,
                                    transactionCount = it[totalCount],
                                )
                            }
                        }
                }
        }
    }

    fun summaryPaymentStatusesMonth(date: LocalDate): Map<String, TransactionsSummary> {
        return summaryPaymentStatusesMonthly()[date.withDayOfMonth(1)] ?: mapOf()
    }
}
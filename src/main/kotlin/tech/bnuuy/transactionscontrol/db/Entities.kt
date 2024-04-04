package tech.bnuuy.transactionscontrol.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.math.BigDecimal
import java.time.LocalDate

class PaymentMethod(id: EntityID<Int>) : IntEntity(id) {
    var name by PaymentMethods.name
    companion object : IntEntityClass<PaymentMethod>(PaymentMethods)

    override fun toString() = name
}

class PaymentStatus(id: EntityID<Int>) : IntEntity(id) {
    var name by PaymentStatuses.name
    companion object : IntEntityClass<PaymentStatus>(PaymentStatuses)

    override fun toString() = name
}

class PaymentType(id: EntityID<Int>) : IntEntity(id) {
    var name by PaymentTypes.name
    companion object : IntEntityClass<PaymentType>(PaymentTypes)

    override fun toString() = name
}

class ProcessingStatus(id: EntityID<Int>) : IntEntity(id) {
    var name by ProcessingStatuses.name
    companion object : IntEntityClass<ProcessingStatus>(ProcessingStatuses)

    override fun toString() = name
}

class Transaction(id: EntityID<Int>) : IntEntity(id) {
    var date by Transactions.date
    var currency by Transactions.currency
    var amount by Transactions.amount
    var costOfRisk by Transactions.costOfRisk
    var paymentType by PaymentType referencedOn Transactions.paymentType
    var paymentMethod by PaymentMethod referencedOn Transactions.paymentMethod
    var processingStatus by ProcessingStatus referencedOn Transactions.processingStatus
    var paymentStatus by PaymentStatus referencedOn Transactions.paymentStatus
    var cardLastFourDigits by Transactions.cardLastFourDigits
    var terminalIp by Transactions.terminalIp
    var payerName by Transactions.payerName
    var countryCode by Transactions.countryCode
    companion object : IntEntityClass<Transaction>(Transactions)
}

data class TransactionsSummary(
    val totalAmount: BigDecimal,
    val transactionCount: Long,
)

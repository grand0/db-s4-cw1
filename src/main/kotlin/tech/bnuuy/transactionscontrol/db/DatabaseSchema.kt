package tech.bnuuy.transactionscontrol.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import tech.bnuuy.transactionscontrol.util.InetColumnType.Companion.inet

object PaymentMethods : IntIdTable("payment_methods") {
    val name = varchar("method_name", 50)
}

object PaymentStatuses : IntIdTable("payment_statuses") {
    val name = varchar("status_name", 50)
}

object PaymentTypes : IntIdTable("payment_types") {
    val name = varchar("type_name", 50)
}

object ProcessingStatuses : IntIdTable("processing_statuses") {
    val name = varchar("status_name", 50)
}

object Transactions : IntIdTable(name = "transactions", columnName = "transaction_id") {
    val date = date("transaction_date")
    val currency = char("currency_type", 3)
    val amount = decimal("amount", 10, 2)
    val costOfRisk = decimal("cost_of_risk", 10, 2)
    val paymentType = reference("payment_type_id", PaymentTypes)
    val paymentMethod = reference("payment_method_id", PaymentMethods)
    val processingStatus = reference("processing_status", ProcessingStatuses)
    val paymentStatus = reference("payment_status", PaymentStatuses)
    val cardLastFourDigits = char("card_last_four_digits", 4)
    val terminalIp = inet("terminal_ip")
    val payerName = varchar("payer_name", 100)
    val countryCode = char("country_code", 2)
}

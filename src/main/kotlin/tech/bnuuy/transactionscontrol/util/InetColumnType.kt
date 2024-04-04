package tech.bnuuy.transactionscontrol.util

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject
import java.net.InetAddress

/**
 * Postgres INET column for storing ipv4 and ipv6 addresses.
 * From https://gist.github.com/christxph/6e2a8d973024c8eb398722542157d6b6
 */
class InetColumnType : ColumnType(nullable = false) {
    override fun sqlType(): String = "INET"

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val pgObject = PGobject()
        pgObject.type = "inet"
        when (value) {
            is InetAddress -> pgObject.value = value.hostAddress
            is String -> pgObject.value = value
            else -> {
                super.setParameter(stmt, index, value)
                return
            }
        }
        stmt.set(index, pgObject)
    }

    override fun valueFromDB(value: Any): Any {
        if (value is PGobject) {
            val pgValue = value.value
            pgValue ?: error("PGobject value is null ($value)")
            return valueFromDB(pgValue)
        }

        return when (value) {
            is String -> InetAddress.getByName(value)
            else -> error("Unknown valueFromDb type ${value.javaClass.name}")
        }
    }

    override fun valueToDB(value: Any?): String {
        value ?: error("Value is null.")
        return notNullValueToDB(value)
    }

    override fun notNullValueToDB(value: Any): String {
        return when (value) {
            is InetAddress -> value.hostAddress
            else -> error("Unknown type ${value.javaClass.name}")
        }
    }

    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is InetAddress -> "'${value.hostAddress}'"
            else -> super.nonNullValueToString(value)
        }
    }

    companion object {
        /**
         * Creates an INET column, with the specified [name],
         * for storing [InetAddress]es. (IPv4 or IPv6 addresses)
         */
        fun Table.inet(name: String): Column<InetAddress> = registerColumn(name, InetColumnType())
    }
}
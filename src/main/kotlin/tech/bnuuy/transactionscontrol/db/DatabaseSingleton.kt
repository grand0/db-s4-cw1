package tech.bnuuy.transactionscontrol.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig

object DatabaseSingleton {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcUrl = System.getenv("DB_JDBC_URL")
        val user = System.getenv("DB_USER")
        val password = System.getenv("DB_PASSWORD")
        Database.connect(
            jdbcUrl,
            driverClassName,
            user,
            password,
            databaseConfig = DatabaseConfig {
                keepLoadedReferencesOutOfTransaction = true
            }
        )
    }
}
package com.yeudaby.calls_counter.backend

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.jackson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureDependencyInjection()
    configureDatabase()
    configureSecurity()
    configureSerialization()
    configureMonitoringAndHealth()
    configureRouting()
}


fun Application.configureDependencyInjection() {
    install(Koin) {
        SLF4JLogger()
        modules(appModule) // Define your koin modules mapping services/repositories here
    }
}

// ==========================================
// 2. Database Connection (Exposed + Postgres)
// ==========================================
fun Application.configureDatabase() {
    // Connects Exposed directly to your PostgreSQL instance
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/your_database",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "your_password"
    )
}

// ==========================================
// 3. Security, CORS, Auth & Rate Limiting
// ==========================================
fun Application.configureSecurity() {
    // Cross-Origin Resource Sharing
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost() // Production warning: replace with specific client domains
    }

    // Rate Limiting Config
    install(RateLimit) {
        global {
            rateLimiter(limit = 60, refillPeriod = 1.minutes)
        }
    }
}

// ==========================================
// 4. Content Negotiation & Serialization
// ==========================================
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        // Handles content types flexibly with both requested formats
        json()    // kotlinx.serialization engine
//        jackson { // Jackson engine configuration fallback/custom overrides
//            // e.g., configure(SerializationFeature.INDENT_OUTPUT, true)
//        }
    }
}

// ==========================================
// 5. Diagnostics & KHealth
// ==========================================
fun Application.configureMonitoringAndHealth() {
    // Configures simple health checkpoints for docker/k8s orchestrators
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "UP"))
        }
    }
}

// ==========================================
// 6. Type-Safe Resources & Routing
// ==========================================
// Annotation declaration matching your "Resources" plugin choice
@Resource("/users")
class UsersResource {
    @Resource("{id}")
    class Id(val parent: UsersResource, val id: Int)
}

// Mock placeholder module configuration for Koin
val appModule = org.koin.dsl.module {
    // single { YourRepository() }
}

package com.yeudaby.calls_counter.backend


import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(Resources) // Enables Type-safe routing structures

    routing {
        // Open endpoint
        get("/") {
            call.respondText("Server is operating normally!")
        }

        // Type-Safe Routing using Resources
        get<UsersResource.Id> { userParam ->
            call.respond(mapOf("userId" to userParam.id, "status" to "fetched"))
        }


//        // Guarded Resource Routes under basic authentication
//        authenticate("auth-basic") {
//            get("/secure-data") {
//                val principal = call.principal<UserIdPrincipal>()
//                call.respondText("Hello authenticated user: ${principal?.name}")
//            }
//        }
    }
}

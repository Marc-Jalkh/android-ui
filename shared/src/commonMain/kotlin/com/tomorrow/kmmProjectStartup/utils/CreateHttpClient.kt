package com.tomorrow.kmmProjectStartup.utils

import com.tomorrow.kmmProjectStartup.domain.model.MultipleFieldValidationError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json



fun createHttpClient(
    httpClientEngine: HttpClientEngine,
    json: Json,
    enableNetworkLogs: Boolean = false,
    authToken: BearerTokens? = null,
    expectSuccess: Boolean = true,
    responseValidator: suspend (Throwable, HttpRequest) -> Unit = responseValidator@{ exception, _ ->
        val clientException = exception as? ClientRequestException ?: return@responseValidator
        val response = clientException.response

        if (response.status == HttpStatusCode.UnprocessableEntity)
            throw clientException.response.body<GeneralValidationError>()
                .toMultipleFieldValidationError()

        if (response.status == HttpStatusCode.Forbidden)
            throw clientException.response.body<GeneralError>()

        return@responseValidator
    }
) = HttpClient(httpClientEngine) {
    defaultRequest { contentType(ContentType.Application.Json) }

    this.expectSuccess = expectSuccess

    HttpResponseValidator { handleResponseExceptionWithRequest(responseValidator) }

    install(ContentNegotiation) { json(json) }

    install(Auth) {
        bearer {
            authToken?.let {
                loadTokens {
                    BearerTokens(
                        accessToken = it.accessToken, refreshToken = it.refreshToken
                    )
                }
            }
        }
    }

    if (enableNetworkLogs) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}

@Serializable
data class GeneralValidationError(
    override val message: String, val errors: Map<String, List<String>>
) : Exception(message)

@Serializable
data class GeneralError(
    override val message: String
) : Exception(message)

fun GeneralValidationError.toMultipleFieldValidationError() = MultipleFieldValidationError(
    errors = this.errors, message = this.message
)

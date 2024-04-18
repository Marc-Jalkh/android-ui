package com.tomorrow.kmmProjectStartup.domain.utils

import com.tomorrow.kmmProjectStartup.utils.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

open class AppStoreHelper(httpClient: HttpClient, json: Json) {
    constructor(engine: HttpClientEngine, json: Json) : this(
        createHttpClient(
            httpClientEngine = engine,
            json = json,
            enableNetworkLogs = false,
        ),
        json
    )
    private val httpClient = httpClient
    private val json = json

    class AppInfo(
        val name: String,
        val updateUrl: String,
    )

    @Serializable
    private class AppleResponse(val results: List<AppstoreInfoDTO>) {
        @Serializable
        class AppstoreInfoDTO(
            val trackName: String,
            val trackViewUrl: String,
        )
    }

    suspend fun getAppNameAndUpdateUrl(bundleId: String): AppInfo? = try {
        this.httpClient.get("https://itunes.apple.com/lookup?bundleId=$bundleId").body<ByteArray>()
            .let { this.json.decodeFromString<AppleResponse>(it.decodeToString()) }
            .results.firstOrNull()?.let { AppInfo(name = it.trackName, updateUrl = it.trackViewUrl) }
    } catch (e: Throwable) {
        println(e.message)
        null
    }
}
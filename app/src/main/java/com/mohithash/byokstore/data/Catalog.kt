package com.mohithash.byokstore.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@Serializable data class ToolInfo(val emoji: String = "", val title: String = "", val subtitle: String = "")
@Serializable
data class AppInfo(
    val id: String, val name: String, val tagline: String = "", val category: String = "", val about: String = "",
    val colors: List<String> = emptyList(), val icon: String = "spark", val `package`: String = "",
    val tools: List<ToolInfo> = emptyList(), val apk: String = "", val aab: String = "", val source: String = "", val listing: String = "", val kind: String = "factory",
)
@Serializable data class Catalog(val generated: String = "", val count: Int = 0, val categories: List<String> = emptyList(), val apps: List<AppInfo> = emptyList())

object CatalogApi {
    const val URL = "https://mohithash.github.io/byok-store/catalog.json"
    private val json = Json { ignoreUnknownKeys = true }
    fun parse(text: String): Catalog = json.decodeFromString(Catalog.serializer(), text)
    suspend fun fetch(): String = withContext(Dispatchers.IO) {
        val c = (java.net.URL(URL).openConnection() as HttpURLConnection).apply { connectTimeout = 15_000; readTimeout = 30_000 }
        try { if (c.responseCode !in 200..299) error("HTTP ${c.responseCode}"); c.inputStream.bufferedReader().readText() } finally { c.disconnect() }
    }
}

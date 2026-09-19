package com.mohithash.byokstore.ui

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohithash.byokstore.App
import com.mohithash.byokstore.data.AppInfo
import com.mohithash.byokstore.data.Catalog
import com.mohithash.byokstore.data.CatalogApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer
import java.io.File

class AppViewModel(private val app: App) : ViewModel() {
    private val cached: StateFlow<String> = app.store.flow("catalog", String.serializer(), "")
    val catalog = MutableStateFlow(cached.value.takeIf { it.isNotBlank() }?.let { runCatching { CatalogApi.parse(it) }.getOrNull() } ?: Catalog())
    val loading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)
    val selected = MutableStateFlow<AppInfo?>(null)
    val query = MutableStateFlow("")
    val category = MutableStateFlow("All")
    /** id → download id while an APK is downloading. */
    val downloading = MutableStateFlow<Map<String, Long>>(emptyMap())

    init { refresh() }

    fun refresh() {
        loading.value = true; error.value = null
        viewModelScope.launch {
            runCatching { CatalogApi.fetch() }.onSuccess { text ->
                runCatching { CatalogApi.parse(text) }.onSuccess { catalog.value = it; app.store.set("catalog", String.serializer(), text) }.onFailure { error.value = "Bad catalog: ${it.message}" }
            }.onFailure { error.value = if (catalog.value.apps.isEmpty()) "Couldn't load the catalog: ${it.message}" else null }
            loading.value = false
        }
    }

    fun filtered(): List<AppInfo> {
        val q = query.value.trim().lowercase(); val c = category.value
        return catalog.value.apps.filter { a -> (c == "All" || a.category == c) && (q.isBlank() || q.split(" ").all { w ->
            (a.name + " " + a.tagline + " " + a.category + " " + a.about + " " + a.tools.joinToString(" ") { it.title + " " + it.subtitle }).lowercase().contains(w) }) }
    }

    fun isInstalled(ctx: Context, a: AppInfo): Boolean = runCatching { ctx.packageManager.getPackageInfo(a.`package`, 0); true }.getOrDefault(false)

    fun openApp(ctx: Context, a: AppInfo) { ctx.packageManager.getLaunchIntentForPackage(a.`package`)?.let { ctx.startActivity(it) } }

    /** Downloads the APK with DownloadManager into the app's external files dir, then hands it to the package installer. */
    fun install(ctx: Context, a: AppInfo) {
        val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val file = File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "${a.id}.apk").also { it.delete() }
        val req = DownloadManager.Request(Uri.parse(a.apk)).setTitle("${a.name} APK").setMimeType("application/vnd.android.package-archive")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED).setDestinationUri(Uri.fromFile(file))
        val id = dm.enqueue(req)
        downloading.value = downloading.value + (a.id to id)
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                val c = dm.query(DownloadManager.Query().setFilterById(id)) ?: break
                val status = if (c.moveToFirst()) c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) else DownloadManager.STATUS_FAILED
                c.close()
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".files", file)
                    ctx.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK))
                    break
                }
                if (status == DownloadManager.STATUS_FAILED) { error.value = "Download failed for ${a.name}"; break }
            }
            downloading.value = downloading.value - a.id
        }
    }
}

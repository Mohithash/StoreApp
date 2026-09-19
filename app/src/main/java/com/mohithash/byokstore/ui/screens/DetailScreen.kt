@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.mohithash.byokstore.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mohithash.byokstore.ui.AppIcon
import com.mohithash.byokstore.ui.AppViewModel
import com.mohithash.byokstore.ui.KeyValue
import com.mohithash.byokstore.ui.Label
import com.mohithash.byokstore.ui.StatCard

@Composable
fun DetailScreen(vm: AppViewModel, onBack: () -> Unit) {
    val sel by vm.selected.collectAsState()
    val a = sel ?: run { onBack(); return }
    val downloading by vm.downloading.collectAsState()
    val cs = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    val installed = vm.isInstalled(ctx, a)
    val busy = a.id in downloading
    fun open(url: String) = ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    Scaffold(topBar = { TopAppBar(title = { Text(a.name) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = cs.surface), navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppIcon(a, 84.dp)
                Column { Text(a.name, style = MaterialTheme.typography.headlineSmall); Text(a.tagline, color = cs.onSurfaceVariant); Text(a.category, style = MaterialTheme.typography.labelLarge, color = cs.primary) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (installed) Button({ vm.openApp(ctx, a) }, shapes = ButtonDefaults.shapes(), modifier = Modifier.weight(1f).height(52.dp)) { Text("Open") }
                else Button({ vm.install(ctx, a) }, enabled = !busy, shapes = ButtonDefaults.shapes(), modifier = Modifier.weight(1f).height(52.dp)) { if (busy) { LoadingIndicator(Modifier.size(20.dp)); Spacer(Modifier.size(8.dp)); Text("Downloading…") } else Text("Install") }
                OutlinedButton({ open(a.apk) }, shapes = ButtonDefaults.shapes(), modifier = Modifier.height(52.dp)) { Text("APK") }
            }
            Text(a.about, style = MaterialTheme.typography.bodyLarge)
            if (a.tools.isNotEmpty()) StatCard { Label("Tools"); a.tools.forEach { t -> Row(Modifier.padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { Text(t.emoji, style = MaterialTheme.typography.titleLarge); Column { Text(t.title, style = MaterialTheme.typography.titleMedium); Text(t.subtitle, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant) } } } }
            StatCard {
                Label("Details")
                KeyValue("Package", a.`package`); KeyValue("Kind", if (a.kind == "bespoke") "Hand-built" else "Factory"); KeyValue("Privacy", "On-device; your key → your provider")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    OutlinedButton({ open(a.listing) }, shapes = ButtonDefaults.shapes(), modifier = Modifier.weight(1f)) { Text("Listing") }
                    OutlinedButton({ open(a.source) }, shapes = ButtonDefaults.shapes(), modifier = Modifier.weight(1f)) { Text("Source") }
                }
            }
            Text("Sideloading: Android will ask you to allow installs from BYOK Store the first time. Every app is signed; add your API key in its Settings after install.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
        }
    }
}

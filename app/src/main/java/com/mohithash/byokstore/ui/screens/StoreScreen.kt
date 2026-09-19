@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.mohithash.byokstore.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mohithash.byokstore.data.AppInfo
import com.mohithash.byokstore.ui.AppIcon
import com.mohithash.byokstore.ui.AppViewModel
import com.mohithash.byokstore.ui.EmptyState
import com.mohithash.byokstore.ui.HeroCard
import com.mohithash.byokstore.ui.Label
import com.mohithash.byokstore.ui.theme.Brand

@Composable
fun StoreScreen(vm: AppViewModel, onOpen: (AppInfo) -> Unit) {
    val catalog by vm.catalog.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val query by vm.query.collectAsState()
    val category by vm.category.collectAsState()
    val cs = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val list = vm.filtered()
    Scaffold(modifier = Modifier.nestedScroll(scroll.nestedScrollConnection), topBar = {
        MediumFlexibleTopAppBar(title = { Text("BYOK Store") }, subtitle = { Text("${catalog.apps.size} apps · your key, your phone") },
            actions = { IconButton(vm::refresh) { Icon(Icons.Default.Refresh, "Refresh") } }, scrollBehavior = scroll, colors = TopAppBarDefaults.topAppBarColors(containerColor = cs.surface, scrolledContainerColor = cs.surface))
    }) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (loading) LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth())
                    HeroCard(colors = listOf(cs.primary, Brand.heroDeep), blobShape = MaterialShapes.Cookie12Sided) {
                        Label("Bring your own key", cs.onPrimary.copy(alpha = 0.8f))
                        Text("No accounts. No ads. No analytics.", style = MaterialTheme.typography.headlineSmall, color = cs.onPrimary)
                        Text("Every app talks straight to the AI provider you choose with your own key. Install, add your key in Settings, done.", color = cs.onPrimary.copy(alpha = 0.9f))
                    }
                    OutlinedTextField(query, { vm.query.value = it }, placeholder = { Text("Search apps, tools, categories") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge)
                    error?.let { Text(it, color = cs.error, style = MaterialTheme.typography.bodySmall) }
                }
            }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("All") + catalog.categories) { c -> FilterChip(selected = category == c, onClick = { vm.category.value = c }, label = { Text(c) }) }
                }
            }
            item { Text("${list.size} apps", style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) }
            if (list.isEmpty() && !loading) item { EmptyState(Icons.Default.Search, "Nothing matches", "Try another word or category.") }
            items(list, key = { it.id }) { a ->
                val installed = vm.isInstalled(ctx, a)
                ListItem(
                    leadingContent = { AppIcon(a) },
                    headlineContent = { Text(a.name, style = MaterialTheme.typography.titleMedium) },
                    supportingContent = { Text(a.tagline, maxLines = 2) },
                    trailingContent = { if (installed) SuggestionChip(onClick = { vm.openApp(ctx, a) }, label = { Text("Open") }) else Text(a.category, style = MaterialTheme.typography.labelSmall, color = cs.primary) },
                    colors = ListItemDefaults.colors(containerColor = cs.surfaceContainerLow),
                    modifier = Modifier.padding(horizontal = 16.dp).clip(MaterialTheme.shapes.large).clickable { onOpen(a) },
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

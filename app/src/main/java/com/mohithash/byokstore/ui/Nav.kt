package com.mohithash.byokstore.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mohithash.byokstore.ui.screens.DetailScreen
import com.mohithash.byokstore.ui.screens.StoreScreen

@Composable
fun Nav(vm: AppViewModel) {
    val nav = rememberNavController()
    NavHost(nav, "store") {
        composable("store") { StoreScreen(vm, onOpen = { vm.selected.value = it; nav.navigate("detail") }) }
        composable("detail") { DetailScreen(vm, onBack = { nav.popBackStack() }) }
    }
}
